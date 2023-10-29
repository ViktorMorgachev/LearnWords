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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningItemsRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource
) {

    val data: Flow<List<LearningItem>> = localDataSource.learningItems.transform<List<LearningItemDB>, List<LearningItem>>{ emit(it.map { it.toLearningItem() }) }

    suspend fun getDataFromDatabase() = localDataSource.fetchDatabaseItems()
        .transform<List<LearningItemDB>, List<LearningItem>>() { emit(it.map { it.toLearningItem() })
    }.flowOn(dispatcher)


    suspend fun fetchDataFromNetwork() = remoteDataSource.fetchDataFromNetwork().transform<Result<List<LearningItemAPI>>, Result<List<LearningItem>>> {
        if (it is Result.Error){
            emit(it)
        }
        if (it is Result.Success){
            emit(Result.Success(it.data.map { it.toLearningItem() }))
        }
    }

    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB())

    suspend fun writeListToLocalDatabase(learningItem: List<LearningItem>) = localDataSource.addLearningItems(learningItem.map { it.toLearningItemDB() })

    suspend fun writeListToRemoteDatabase(learningItem: List<LearningItem>) = flow<Result<Nothing>> {
        emit(remoteDataSource.addLearningItems(learningItem.map { it.toLearningItemAPI() }))
    }.flowOn(dispatcher)

}