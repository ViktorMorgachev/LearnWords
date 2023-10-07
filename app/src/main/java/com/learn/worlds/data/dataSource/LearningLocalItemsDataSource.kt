package com.learn.worlds.data.dataSource

import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(@IoDispatcher private val coroutineDispatcher: CoroutineDispatcher, private val learningItemDao: LearningItemDao): LearningItemsDataSource {


    override val learningItems:  Flow<List<LearningItemDB>> = learningItemDao.getLearningItems()

    override suspend fun changeState(newState: String, learningItemID: Int) {
        // TODO: Will write test later, I don't think to add wrapper for ui if will Fail
        withContext(coroutineDispatcher){
            learningItemDao.getLearningItem(learningItemID).collect{
                try {
                    val newItem = it.copy(learningStatus = newState)
                    learningItemDao.insertLearningItem(newItem)
                } catch (t: Throwable){
                    Timber.e(t)
                }
            }
        }

    }

    override suspend fun addLearningItem(learningItemDB: LearningItemDB) {
        withContext(coroutineDispatcher){
            try {
                learningItemDao.insertLearningItem(learningItemDB)
            } catch (t: Throwable){
                Timber.e(t)
            }

        }
    }


}