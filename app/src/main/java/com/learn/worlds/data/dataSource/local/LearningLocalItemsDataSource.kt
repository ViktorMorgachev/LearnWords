package com.learn.worlds.data.dataSource.local


import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val learningItemDao: LearningItemDao
) {

    val learningItems = flow<List<LearningItemDB>> { emit(learningItemDao.getLearningItems()) }.flowOn(Dispatchers.IO)


   suspend fun fetchDatabaseItems() = flow<List<LearningItemDB>> { emit(learningItemDao.getLearningItems()) }.flowOn(Dispatchers.IO)

    suspend fun addLearningItem(learningItemDB: LearningItemDB) = flow<Result<LearningItemDB>> {
        Timber.d("addLearningItem: learningItem $learningItemDB")
        try {
            learningItemDao.insertLearningItem(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(Dispatchers.IO)

    suspend fun addLearningItems(learningItemDB: List<LearningItemDB>) = flow<Result<List<LearningItemDB>>> {
            Timber.d("addLearningItems: learningItem $learningItemDB")
            try {
                learningItemDao.insertLearningItems(learningItemDB)
                emit(Result.Complete)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Result.Error())
            }
        }.flowOn(Dispatchers.IO)

}