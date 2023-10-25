package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource
) {

    val data: Flow<Result<List<LearningItem>>> = localDataSource.learningItems.map { initialData ->
        try {
            Result.Success(initialData.map { it.toLearningItem() })
        } catch (t: Throwable) {
            Timber.e(t)
            Result.Success(listOf())
        }
    }

    fun getDataFromDatabase() = localDataSource.learningItems
    suspend fun fetchDataFromNetwork() = flow {
        remoteDataSource.fetchDataFromNetwork()
            .filter { it != Result.Loading && it != Result.Complete }
            .onEach { result ->
                if (result is Result.Success) {
                    emit(Result.Success(result.data.map { it.toLearningItem() }))
                }
                if (result is Result.Error) {
                    emit(Result.Error())
                }
            }.collect()
    }

    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB())

    suspend fun writeListToRemoteDatabase(learningItem: List<LearningItem>) = flow<Result<List<LearningItem>>> {
        emit(remoteDataSource.addLearningItems(learningItem.map { it.toLearningItemAPI() }))
    }


    suspend fun writeToLocalDatabase(dataFromNetwork: List<LearningItem>) =
        flow<Result<List<LearningItem>>> {
            data.collect { databaseItems ->
                if (databaseItems == Result.Error()) {
                    emit(Result.Error())
                } else {
                    localDataSource.addLearningItems(dataFromNetwork.map { it.toLearningItemDB() }).collectLatest {
                        emit(it)
                    }
                }
            }
        }

}