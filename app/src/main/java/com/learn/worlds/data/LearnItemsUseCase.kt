package com.learn.worlds.data

import android.content.Context
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val preferences: MySharedPreferences,
    private val learningItemsRepository: LearningItemsRepository
) {

    val actualData = learningItemsRepository.data
    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<Any>> {
        Timber.e("learningItem $learningItem")
        if (preferences.dataBaseLocked) {
            emit(Result.Error(ErrorType.DATABASE_LIMITS))
        } else {
            try {
               var result: Result<Any> = Result.Complete
                learningItemsRepository.writeToLocalDatabase(learningItem).collect{
                    result = it
                }
                emit(result)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Result.Error())
            }
        }
    }.flowOn(dispatcher)


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