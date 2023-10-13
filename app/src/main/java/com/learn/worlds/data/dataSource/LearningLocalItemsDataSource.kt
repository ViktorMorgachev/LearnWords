package com.learn.worlds.data.dataSource

import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, private val learningItemDao: LearningItemDao):
    LearningItemsDataSource {


    override val learningItems:  Flow<List<LearningItemDB>>  = learningItemDao.getLearningItems()

    init {
        Timber.d("Init ${this.javaClass.simpleName}")
    }

    override suspend fun changeState(newState: String, learningItemID: Int) = flow{
        // TODO: Will write test later
        learningItemDao.getLearningItem(learningItemID).collect{
            try {
                emit(Result.Loading)
                val newItem = it.copy(learningStatus = newState)
                learningItemDao.insertLearningItem(newItem)
                emit(Result.Complete)
            } catch (t: Throwable){
                Timber.e(t)
                emit(Result.Error())
            }
        }

    }

    override suspend fun addLearningItem(learningItemDB: LearningItemDB)  = flow {
        try {
            emit(Result.Loading)
            learningItemDao.insertLearningItem(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }
    }

}