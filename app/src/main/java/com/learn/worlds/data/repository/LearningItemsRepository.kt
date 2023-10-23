package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val authService: AuthService,
    private val preferences: MySharedPreferences,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource) {

    private val scope: CoroutineScope = CoroutineScope(dispatcher)

    val data: Flow<Result<List<LearningItem>>> = localDataSource.learningItems.map { initialData ->
            try {
                Result.Success(initialData.map { it.toLearningItem() })
            } catch (t: Throwable) {
                Result.Error()
            }
        }

    init {
        if (authService.isAuthentificated()){
            scope.launch{
                remoteDataSource.learningItems.collect {
                    try {
                        if (it is Result.Success<List<LearningItemAPI>>){
                            localDataSource.addLearningItems(it.data.map { it.toLearningItemDB() })
                            preferences.isSynchronizedFromRemote = true
                        }
                    } catch (t: Throwable){
                        Timber.e(t)
                    }

                }
            }
        }

    }

    suspend fun addLearningItem(learningItem: LearningItem) = remoteDataSource.addLearningItem(learningItem.toLearningItemAPI())

}