package com.learn.worlds.data


import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.prefs.SynckSharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.data.repository.LearningSynchronizationRepository
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    private val authService: AuthService,
    private val synkPreferences: SynckSharedPreferences,
    private val learningItemsRepository: LearningItemsRepository,
    private val learningSynchronizationRepository: LearningSynchronizationRepository
) {

    suspend fun actualData() = learningItemsRepository.data

    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<Any>> {
        Timber.d("learningItem $learningItem")
        try {
            if (authService.isAuthentificated()) {
                learningItemsRepository.writeToRemoteDatabase(learningItem)
            }
            learningItemsRepository.writeToLocalDatabase(learningItem).collect {
                emit(it)
            }
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }

    /*1. Пишем данные из преференсов как есть заменяя данные в базе удалённой
    * 2. Чистим локальную базу, учитывая флаг deletedStatus
    * 3. После уже синхронизируем как обычно*/
    suspend fun synckItems() = flow<Result<List<LearningItem>>> {
        val remoteData = mutableListOf<LearningItem>()
        val itemsForRemoving = synkPreferences.getActualLearnItemsSynronization()
        var resultMarkedItems: Result<Nothing>? = null
        if (itemsForRemoving.isNotEmpty()) {
            learningSynchronizationRepository.replaceRemoteItems(itemsForRemoving).collectLatest {
                resultMarkedItems = it
            }
            if (resultMarkedItems == null || resultMarkedItems is Result.Error) {
                emit(Result.Error())
                return@flow
            }
        }
        synkPreferences.removeAllItemsIdsForSynshronization()

        var itemsRemovedIds = listOf<Long>()
        learningSynchronizationRepository.fetchItemsIdsForRemoving().collectLatest {
            if (it is Result.Success && it.data.isNotEmpty()) {
                itemsRemovedIds = it.data
            }
        }

        if (itemsRemovedIds.isNotEmpty()) {
            learningItemsRepository.removeItemsFromLocalDatabase(itemsRemovedIds).collect {}
        }

        learningItemsRepository.fetchDataFromNetwork(needIgnoreRemovingItems = true)
            .collectLatest {
                if (it is Result.Success) {
                    remoteData.addAll(it.data)
                } else {
                    remoteData.addAll(listOf())
                }
            }
        val localData = mutableListOf<LearningItem>()
        learningItemsRepository.getDataFromDatabase().collect {
            localData.addAll(it)
        }
        val dataForNetwork: MutableList<LearningItem> = mutableListOf()
        val dataForLocal: MutableList<LearningItem> = mutableListOf()
        localData.plus(remoteData).forEach {
            if (!localData.contains(it)) {
                dataForLocal.add(it)
            }
            if (!remoteData.contains(it)) {
                dataForNetwork.add(it)
            }
        }
        Timber.d(
            "dataForRemote: ${dataForNetwork.joinToString(", ")} " +
                    "\ndataForLocal: ${dataForLocal.joinToString(", ")}  "
        )

        val synckResult = mutableListOf<Result<Nothing>>()
        learningItemsRepository.writeListToLocalDatabase(dataForLocal).collect {
            synckResult.add(it)
        }
        learningItemsRepository.writeListToRemoteDatabase(dataForNetwork).collect {
            synckResult.add(it)
        }
        if (synckResult.any { it is Result.Error }) {
            emit(synckResult.first { it is Result.Error })
        } else {
            if (synckResult.any { it is Result.Complete }) {
                emit(Result.Success(listOf()))
            } else {
                emit(Result.Complete)
            }
        }
    }

    suspend fun deleteWordItem(learningItem: LearningItem) = flow<Result<Nothing>> {
        val itemForSynshronization = learningItem.toLearningItemAPI().copy(deletedStatus = true)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Complete)
    }

    private fun getActualItemForSynk(learningItem: LearningItem): LearningItemAPI{
        var itemForSynshronization = synkPreferences.getActualLearnItemsSynronization().firstOrNull { it.timeStampUIID == learningItem.timeStampUIID }
        return  itemForSynshronization ?: learningItem.toLearningItemAPI()
    }

    suspend fun changeItemsStatus(learningItem: LearningItem) = flow<Result<Long>> {
        var itemForSynshronization = getActualItemForSynk(learningItem).copy(learningStatus = learningItem.learningStatus)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        learningItemsRepository.writeToLocalDatabase(itemForSynshronization.toLearningItem()).collect{}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Success(learningItem.timeStampUIID))
    }


}