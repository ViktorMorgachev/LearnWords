package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transform
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
    }.flowOn(Dispatchers.IO)

    suspend fun getDataFromDatabase() = localDataSource.fetchDatabaseItems()
        .transform<List<LearningItemDB>, List<LearningItem>>() { emit(it.map { it.toLearningItem() })
    }.flowOn(Dispatchers.IO)


    suspend fun fetchDataFromNetwork() = remoteDataSource.fetchDataFromNetwork().transform<Result<List<LearningItemAPI>>, Result<List<LearningItem>>> {
        if (it is Result.Error){
            emit(it)
        }
        if (it is Result.Success){
            emit(Result.Success(it.data.map { it.toLearningItem() }))
        }
    }

    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB()).transform<Result<LearningItemDB>, Result<LearningItem>> {
        if (it is Result.Success){
            emit(Result.Success(it.data.toLearningItem()))
        }
        if (it is Result.Complete){
            emit(Result.Complete)
        }
        if (it is Result.Error){
            emit(it)
        }
    }

    suspend fun writeListToLocalDatabase(learningItem: List<LearningItem>) = localDataSource.addLearningItems(learningItem.map { it.toLearningItemDB() }).transform<Result<List<LearningItemDB>>, Result<List<LearningItem>>> {
        if (it is Result.Success){
            emit(Result.Success(it.data.map { it.toLearningItem() }))
        }
        if (it is Result.Complete){
            emit(Result.Complete)
        }
        if (it is Result.Error){
            emit(it)
        }
    }

    suspend fun writeListToRemoteDatabase(learningItem: List<LearningItem>) = flow<Result<List<LearningItem>>> {
        emit(remoteDataSource.addLearningItems(learningItem.map { it.toLearningItemAPI() }))
    }.flowOn(Dispatchers.IO)

}