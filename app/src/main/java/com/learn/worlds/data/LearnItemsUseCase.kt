package com.learn.worlds.data


import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository
) {

    suspend fun actualData() = learningItemsRepository.data

    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<Any>> {
        Timber.d("learningItem $learningItem")
        try {
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
        if (synckResult.any{ it == Result.Error()}){
            emit(synckResult.first { it == Result.Error() })
        } else {
            if (synckResult.any { it == Result.Complete }){
                emit(Result.Success(listOf()))
            } else{
                emit(Result.Complete)
            }
        }
    }


}