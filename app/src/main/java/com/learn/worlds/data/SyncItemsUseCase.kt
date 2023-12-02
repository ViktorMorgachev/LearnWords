package com.learn.worlds.data



import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import com.learn.worlds.data.prefs.SynckSharedPreferencesProfile
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.data.repository.LearningSynchronizationRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class SyncItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val synkLearnCardPreferences: SynckSharedPreferencesLearnCards,
    private val profileUseCase: ProfileUseCase,
    private val synkPrefsProfile: SynckSharedPreferencesProfile,
    private val learningItemsRepository: LearningItemsRepository,
    private val learningSynchronizationRepository: LearningSynchronizationRepository
) {


    private val scope = CoroutineScope(dispatcher)

    /*1. Пишем данные из преференсов как есть заменяя данные в базе удалённой
    * 2. Чистим локальную базу, учитывая флаг deletedStatus
    * 3. После уже синхронизируем как обычно*/
    suspend fun synckItems() = flow<Result<List<LearningItem>>> {
        val jobsForRunningAndForget = mutableListOf<Deferred<Unit>>()
        val itemsForRemoving = synkLearnCardPreferences.getActualLearnItemsSynronization()
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
        synkLearnCardPreferences.removeAllItemsIdsForSynshronization()

        jobsForRunningAndForget.add(scope.async(start = CoroutineStart.LAZY) {
            val allMp3FileNames = synkLearnCardPreferences.getActualMp3NamesItemsSynronization()
            if (allMp3FileNames.data.isNotEmpty()) {
                Timber.d("Synchronization: allMp3Files: ${allMp3FileNames.data.map { it }.joinToString(",\n")}")
                learningSynchronizationRepository.uploadAllMp3Files(allMp3FileNames).collectLatest {}
            }
        })

        jobsForRunningAndForget.add(scope.async(start = CoroutineStart.LAZY){
            val allImagesForFirebase = synkLearnCardPreferences.getActualImageNamesItemsSynronization()
            if (allImagesForFirebase.data.isNotEmpty()) {
                Timber.d("Synchronization: allImages ${allImagesForFirebase.data.map { it }.joinToString(",\n")}")
                learningSynchronizationRepository.uploadImageFiles(allImagesForFirebase).collectLatest {}
            }
        })

        jobsForRunningAndForget.add(scope.async(start = CoroutineStart.LAZY) {
            if(!synkPrefsProfile.profileUpdated){
                profileUseCase.updateProfile(profile = synkPrefsProfile.getProfile()!!).collectLatest {  }
            } else {
                profileUseCase.initActualProfile().collectLatest {  }
            }
        })

        jobsForRunningAndForget.add(scope.async(start = CoroutineStart.LAZY){
            var itemsRemovedIds = listOf<Long>()
            Timber.d("Synchronization: forRemovingCards start")
            learningSynchronizationRepository.fetchItemsIdsForRemoving().collectLatest {
                if (it is Result.Success && it.data.isNotEmpty()) {
                    itemsRemovedIds = it.data
                }
            }
            if (itemsRemovedIds.isNotEmpty()) {
                learningItemsRepository.removeItemsFromLocalDatabase(itemsRemovedIds).collect {}
            }
            Timber.d("Synchronization: forRemovingCards done")
        })
        jobsForRunningAndForget.forEach { it.start() }

        val remoteCardsJob = scope.async(start = CoroutineStart.LAZY){
            var result: List<LearningItem> = listOf()
            learningItemsRepository.fetchDataFromNetwork(needIgnoreRemovingItems = true).collectLatest {
                if (it is Result.Success) {
                    result = it.data
                } else {
                    result = listOf()
                }
            }
            result
        }

        val localCardsJob = scope.async(start = CoroutineStart.LAZY){
            var result: List<LearningItem> = listOf()
            learningItemsRepository.getDataFromDatabase().collectLatest {
                result =   it
            }
            result
        }
        remoteCardsJob.start()
        localCardsJob.start()

        val localData = localCardsJob.await()
        val remoteData = remoteCardsJob.await()

        val dataForNetwork: MutableList<LearningItem> =   mutableListOf()
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
            "dataForRemote: ${dataForNetwork.joinToString(", ")} \n" +
                    "dataForLocal: ${dataForLocal.joinToString(", ")}  "
        )

        val resultToWriteLocalDatabase = scope.async(){
            var result: Result<Nothing> = Result.Loading
            Timber.d("Synchronization: writeListToLocalDatabase")
            if (dataForLocal.isNotEmpty()) {
                learningItemsRepository.writeListToLocalDatabase(dataForLocal).collect {
                    result = it
                }
            }
            result
        }

        val resultToWriteRemoteDatabase = scope.async(){
            var result: Result<Nothing> = Result.Loading
            Timber.d("Synchronization: writeListToRemoteDatabase")
            if (dataForNetwork.isNotEmpty()) {
                learningItemsRepository.writeListToRemoteDatabase(dataForNetwork).collect {
                    result = it
                }
            }
            result
        }


        val synckResult = mutableListOf<Result<Nothing>>(
            resultToWriteLocalDatabase.await(),
            resultToWriteRemoteDatabase.await())


        if (synckResult.any { it is Result.Error }) {
            emit(synckResult.first { it is Result.Error })
        } else {
            if (synckResult.any { it is Result.Complete }) {
                emit(Result.Success(listOf()))
            } else {      
                emit(Result.Complete)
            }
        }
    }.flowOn(dispatcher)

}