package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.mock.LearningMockItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningSynchronizationRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource,
    private val mockItemsDataSource: LearningMockItemsDataSource
) {

    suspend fun replaceRemoteItem(learningItem: LearningItemAPI) = remoteDataSource.replaceRemoteItems(learningItem)

    suspend fun fetchItemsIdsForRemoving() = remoteDataSource.fetchItemsIdsForRemoving()

    suspend fun replaceRemoteItems(learningItems: List<LearningItemAPI>) = flow<Result<Nothing>> {
        val resultList: MutableList<Result<Nothing>> = mutableListOf()
        learningItems.forEach{
            resultList.add(remoteDataSource.replaceRemoteItems(it))
        }
        if (resultList.all { it is Result.Complete }){
            emit(Result.Complete)
        } else {
            emit(Result.Error())
        }
    }.flowOn(dispatcher)


}