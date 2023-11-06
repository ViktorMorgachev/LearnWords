package com.learn.worlds.data.dataSource.local


import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val learningItemDao: LearningItemDao
) {
    val learningItems = learningItemDao.getLearningItemsFlow().flowOn(dispatcher)
   suspend fun fetchDatabaseItems() = flow<List<LearningItemDB>>{
       emit(learningItemDao.getLearningItems())
   }.flowOn(dispatcher)

    suspend fun addLearningItem(learningItemDB: LearningItemDB) = flow<Result<Nothing>> {
        Timber.d("addLearningItem: learningItem $learningItemDB")
        try {
            learningItemDao.insertLearningItem(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

    suspend fun removeItemByIDs(learningItemID: Long) = flow<Result<Nothing>> {
        Timber.d("removeItemByIDs: learningItemID $learningItemID")
        try {
            learningItemDao.deleteLearningItem(learningItemID)
            emit(Result.Complete)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

    suspend fun removeItemsByIDs(learningItemIDs: List<Long>) = flow<Result<Nothing>> {
        Timber.d("removeItemsByIDs: learningItemIDs ${learningItemIDs.joinToString(", ")}}")
        try {
            learningItemIDs.forEach {
                learningItemDao.deleteLearningItem(it)
            }
            emit(Result.Complete)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

    suspend fun addLearningItems(learningItemDB: List<LearningItemDB>) = flow<Result<Nothing>> {
            Timber.d("addLearningItems: learningItem $learningItemDB")
            try {
                learningItemDao.insertLearningItems(learningItemDB)
                emit(Result.Complete)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Result.Error())
            }
        }.flowOn(dispatcher)

}