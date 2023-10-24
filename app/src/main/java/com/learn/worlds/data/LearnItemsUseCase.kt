package com.learn.worlds.data

import android.content.Context
import com.learn.worlds.R
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val preferences: MySharedPreferences,
    private val learningItemsRepository: LearningItemsRepository
) {

    val actualData = learningItemsRepository.data
    suspend fun addLearningItem(learningItem: LearningItem) = flow {
        Timber.e("learningItem $learningItem")
        if (preferences.dataBaseLocked) {
            emit(Result.Error(context.getString(R.string.error_limits_adding_words)))
        } else {
            try {
                learningItemsRepository.writeToLocalDatabase(learningItem).collect()
                emit(Result.Complete)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Result.Error())
            }
        }
    }.flowOn(dispatcher)

    suspend fun syncItems() = flow<Result<List<LearningItem>>>{
        learningItemsRepository.fetchDataFromNetwork()
            .filter { it != Result.Loading && it != Result.Complete }
            .combine(learningItemsRepository.getDataFromDatabase()
                .map { listDB-> listDB.map { it.toLearningItem() } }) { fromNetwork, fromDatabase ->

                if (fromNetwork is Result.Success){
                    val networkData = fromNetwork.data
                    val dataForNetwork: MutableList<LearningItem> = mutableListOf()
                    val dataForLocal: MutableList<LearningItem> = mutableListOf()
                    fromDatabase.forEach { dbItem ->
                        // Записываем в интернет если в ней нет элемента
                        if (!networkData.contains(dbItem)) {
                            dataForNetwork.add(dbItem)
                        }
                    }
                    networkData.forEach { remoteItem ->
                        if (!fromDatabase.contains(remoteItem)) {
                            dataForLocal.add(remoteItem)
                        }
                    }

                    // Если оба списка пусты
                    if (networkData.isEmpty() && dataForLocal.isEmpty()){
                        emit(Result.Success(listOf()))
                    } else {
                        learningItemsRepository.writeToRemoteDatabase(dataForNetwork)
                            .combine(learningItemsRepository.writeToLocalDatabase(dataForLocal)) { remoteResult, localResult->
                                emit(Result.Success(networkData.plus(dataForLocal)))
                            }

                    }



                    Timber.d("SynchronizationWorker: " +
                            "\n forRemote: ${dataForNetwork.joinToString(", ")}" +
                            " \n forLocal: ${dataForLocal.joinToString(", ")}"
                    )

                }
            }.flowOn(dispatcher).collect()
    }


}