package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.mock.LearningMockItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val authService: AuthService,
    private val preferences: MySharedPreferences,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource,
    private val mockDataSource: LearningMockItemsDataSource
) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)

    val data: Flow<Result<List<LearningItem>>> = localDataSource.learningItems.map { initialData ->
        try {
            Result.Success(initialData.map { it.toLearningItem() })
        } catch (t: Throwable) {
            Result.Error()
        }
    }

    suspend fun fetchDataFromNetwork(): Flow<Result<List<LearningItem>>> {
        return flow {
            remoteDataSource.fetchDataFromNetwork().onEach { result ->
                Timber.d("fetchDataFromNetwork: $result")
                when (result) {
                    is Result.Success -> {
                        emit(Result.Success(result.data.map { it.toLearningItem() }))
                    }

                    is Result.Complete -> {
                        emit(Result.Complete)
                    }

                    is Result.Error -> {
                        emit(Result.Loading)
                    }

                    is Result.Loading -> {
                        emit(Result.Loading)
                    }

                }
            }.collect()
        }
    }

    suspend fun writeToLocalDatabase(learningItems: List<LearningItem>) = flow<Result<List<LearningItem>>> {
            data.collect { databaseResult ->
                when (databaseResult) {
                    Result.Complete -> {}
                    is Result.Error -> { emit(Result.Error()) }
                    Result.Loading -> {}
                    is Result.Success -> {
                        learningItems.forEach {

                        }
                    }
                }

            }

        }


    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB())

}