package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.mock.LearningMockItemsDataSource
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
    private val remoteDataSource: LearningRemoteItemsDataSource,
    private val mockItemsDataSource: LearningMockItemsDataSource
) {

    val data: Flow<List<LearningItem>> = localDataSource.learningItems.transform<List<LearningItemDB>, List<LearningItem>>{ emit(it.map { it.toLearningItem() }) }

    suspend fun getDataFromDatabase() = localDataSource.fetchDatabaseItems()
        .transform<List<LearningItemDB>, List<LearningItem>>() { emit(it.map { it.toLearningItem() })
    }.flowOn(dispatcher)


    suspend fun fetchDataFromNetwork(needIgnoreRemovingItems: Boolean = true) = remoteDataSource.fetchDataFromNetwork(needIgnoreRemovingItems).transform<Result<List<LearningItemAPI>>, Result<List<LearningItem>>> {
        if (it is Result.Error){
            emit(it)
        }
        if (it is Result.Success){
            emit(Result.Success(it.data.map { it.toLearningItem() }))
        }
    }

    suspend fun removeItemFromLocalDatabase(itemID: Long) = localDataSource.removeItemByIDs(learningItemID = itemID)

    suspend fun removeItemsFromLocalDatabase(itemIDs: List<Long>) = localDataSource.removeItemsByIDs(learningItemIDs = itemIDs)

    suspend fun markItemStatusRemoved(itemID: Long) = remoteDataSource.markItemsStatusRemoved(itemID)

    suspend fun markItemsStatusRemoved(itemIDs: List<Long>) = flow<Result<Nothing>> {
        val resultList: MutableList<Result<Nothing>> = mutableListOf()
        itemIDs.forEach{
            resultList.add(remoteDataSource.markItemsStatusRemoved(it))
        }
        if (resultList.all { it is Result.Complete }){
            emit(Result.Complete)
        } else {
            emit(Result.Error())
        }
    }.flowOn(dispatcher)
    suspend fun fetchItemsIdsForRemoving() = remoteDataSource.fetchItemsIdsForRemoving()

    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB())

    suspend fun writeToRemoteDatabase(learningItem: LearningItem) = remoteDataSource.addItem(learningItem.toLearningItemAPI())

    suspend fun writeListToLocalDatabase(learningItem: List<LearningItem>) = localDataSource.addLearningItems(learningItem.map { it.toLearningItemDB() })

    suspend fun writeListToRemoteDatabase(learningItems: List<LearningItem>) = flow<Result<Nothing>> {
        val resultList: MutableList<Result<Nothing>> = mutableListOf()
        learningItems.forEach{
            resultList.add(remoteDataSource.addItem(it.toLearningItemAPI()))
        }
        if (resultList.all { it is Result.Complete }){
            emit(Result.Complete)
        } else {
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

}