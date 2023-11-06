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


    // TODO: Переделать с учётом сохранённых данных в prefs
    //  1. Самым первым методом запускать метод удаления данных с remote базой,
    //  в случае ошибки и наличии данных для удаления с базы сразу прекращаь работу */
    suspend fun synckItems() = flow<Result<List<LearningItem>>> {
        val remoteData = mutableListOf<LearningItem>()
        val itemsForRemoving = synkPreferences.getActualLearnItemsForRemoving()
       val removingResult =  learningItemsRepository.removeItemListFromRemoteDatabase(itemsForRemoving.map { it.toLong() })
        if(removingResult is Result.Error){
            emit(Result.Error())
            return@flow
        }
        synkPreferences.removeAllItemsIdsForRemoving()

        learningItemsRepository.fetchDataFromNetwork()
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
        synkPreferences.addWordForRemoving(itemID.toString())
        learningItemsRepository.removeItemFromLocalDatabase(itemID).collectLatest {
            if (it is Result.Complete){
                synkPreferences.removeItemForRemoving(itemID.toString())
            }
        }
        learningItemsRepository.removeItemFromRemoteDatabase(itemID)
        emit(Result.Success(itemID))
    }


}