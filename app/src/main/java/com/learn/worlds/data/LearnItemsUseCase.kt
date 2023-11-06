package com.learn.worlds.data


import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.SynckSharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    private val authService: AuthService,
    private val synkPreferences: SynckSharedPreferences,
    private val learningItemsRepository: LearningItemsRepository
) {

    suspend fun actualData() = learningItemsRepository.data

    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<Any>> {
        Timber.d("learningItem $learningItem")
        try {
            if (authService.isAuthentificated()){
                learningItemsRepository.writeToRemoteDatabase(learningItem)
            }
            learningItemsRepository.writeToLocalDatabase(learningItem).collect{
                emit(it)
            }
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }

    suspend fun synckItems() = flow<Result<List<LearningItem>>> {
        val remoteData = mutableListOf<LearningItem>()
        val itemsForRemoving = synkPreferences.getActualLearnItemsForRemoving()
        var resultMarkedItems: Result<Nothing>? = null
        if (itemsForRemoving.isNotEmpty()){
            learningItemsRepository.markItemsStatusRemoved(itemsForRemoving.map { it.toLong() }).collectLatest {
                resultMarkedItems = it
            }
            if(resultMarkedItems == null || resultMarkedItems is Result.Error ){
                emit(Result.Error())
                return@flow
            }
        }

        synkPreferences.removeAllItemsIdsForRemoving()

        var itemsRemovedIds = listOf<Long>()
        learningItemsRepository.fetchItemsIdsForRemoving().collectLatest {
            if (it is Result.Success && it.data.isNotEmpty()){
               itemsRemovedIds = it.data
            }
        }

        Timber.d("Item ids for removing: ${itemsRemovedIds.joinToString(", ")}}")

        if(itemsRemovedIds.isNotEmpty()){
            learningItemsRepository.removeItemsFromLocalDatabase(itemsRemovedIds).collect{}
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
        learningItemsRepository.getDataFromDatabase().collect{
            localData.addAll(it)
        }
        val dataForNetwork: MutableList<LearningItem> = mutableListOf()
        val dataForLocal: MutableList<LearningItem> = mutableListOf()
        localData.plus(remoteData).forEach {
            if (!localData.contains(it)){
                dataForLocal.add(it)
            }
            if (!remoteData.contains(it)){
                dataForNetwork.add(it)
            }
        }
        Timber.d("dataForRemote: ${dataForNetwork.joinToString(", ")} " +
                "\ndataForLocal: ${dataForLocal.joinToString(", ")}  ")

        val synckResult = mutableListOf<Result<Nothing>>()
        learningItemsRepository.writeListToLocalDatabase(dataForLocal).collect {
            synckResult.add(it)
        }
        learningItemsRepository.writeListToRemoteDatabase(dataForNetwork).collect {
            synckResult.add(it)
        }
        if (synckResult.any { it is Result.Error}){
            emit(synckResult.first { it is Result.Error })
        } else {
            if (synckResult.any { it is Result.Complete }){
                emit(Result.Success(listOf()))
            } else{
                emit(Result.Complete)
            }
        }
    }

   suspend fun deleteWordItem(itemID: Long) = flow<Result<Long>>{

        learningItemsRepository.removeItemFromLocalDatabase(itemID).collect{}
        val result = learningItemsRepository.markItemStatusRemoved(itemID)
       if (result is Result.Error){
           synkPreferences.addWordForRemoving(itemID.toString())
       }
        emit(Result.Success(itemID))
    }


}