package com.learn.worlds.data.dataSource.local


import com.learn.worlds.data.model.base.LearningItem
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
    private val learningItemDao: LearningItemDao) {

     val learningItems = learningItemDao.getLearningItems()

    suspend fun addLearningItem(learningItemDB: LearningItemDB)  = flow<Result<List<LearningItemDB>>> {
        Timber.e("addLearningItem: learningItem $learningItemDB")
        try {
            learningItemDao.insertLearningItem(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

    suspend fun addLearningItems(learningItemDB: List<LearningItemDB>)  = flow<Result<List<LearningItem>>> {
        Timber.e("addLearningItem: learningItem $learningItemDB")
        try {
            learningItemDao.insertLearningItems(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }
    }.flowOn(dispatcher)

}