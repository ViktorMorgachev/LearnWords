package com.learn.worlds.data

import android.content.Context
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.lang.UnsupportedOperationException
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val preferences: MySharedPreferences,
    private val learningItemsRepository: LearningItemsRepository
) {

    val actualData = learningItemsRepository.data
    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<LearningItem>> {
        Timber.e("learningItem $learningItem")
        if (preferences.dataBaseLocked) {
            emit(Result.Error(ErrorType.DATABASE_LIMITS))
        } else {
            try {
               var result: Result<LearningItem> = Result.Complete
                learningItemsRepository.writeToLocalDatabase(learningItem).collect{ result = it }
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
        val databaseItems = mutableListOf<LearningItem>()
        learningItemsRepository.getDataFromDatabase().collect{
            databaseItems.addAll(it)
        }
        val dataForNetwork: MutableList<LearningItem> = mutableListOf()
        val dataForLocal: MutableList<LearningItem> = mutableListOf()
        databaseItems.forEach { dbItem ->
            if (!remoteData.contains(dbItem)) {
                dataForNetwork.add(dbItem)
            }
        }
        remoteData.forEach { remoteItem ->
            if (!databaseItems.contains(remoteItem)) {
                dataForLocal.add(remoteItem)
            }
        }
        Timber.d("dataForRemote: ${dataForNetwork.joinToString(", ")} " +
                "\ndataForLocal: ${dataForLocal.joinToString(", ")}  ")

        if (dataForNetwork.isEmpty() && dataForLocal.isEmpty()) {
            emit(Result.Success(listOf<LearningItem>()))
        } else {
            val savedToRemoteItems = mutableListOf<LearningItem>()
            learningItemsRepository.writeListToLocalDatabase(dataForLocal)
                .transform<Result<List<LearningItem>>, List<LearningItem>> {
                    if (it is Result.Success) emit(it.data) else emit(listOf())
                }.collect {
                    savedToRemoteItems.addAll(it)
                }
            val savedToDatabaseItems = mutableListOf<LearningItem>()
            learningItemsRepository.writeListToRemoteDatabase(dataForNetwork)
                .transform<Result<List<LearningItem>>, List<LearningItem>>
                {
                    if (it is Result.Success) emit(it.data) else emit(listOf())
                }.collect {
                    savedToDatabaseItems.addAll(it)
                }
            emit(Result.Success(savedToDatabaseItems.plus(savedToRemoteItems)))
        }
    }


}