package com.learn.worlds.data.dataSource

import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(@IoDispatcher private val coroutineDispatcher: CoroutineDispatcher, private val learningItemDao: LearningItemDao): LearningItemsDataSource {


    override val learningItems:  Flow<List<LearningItemDB>> =  learningItemDao.getLearningItems()

    override suspend fun changeState(newState: LearningStatus, learningItemID: Int) {
        withContext(coroutineDispatcher){
            learningItemDao.getLearningItem(learningItemID).collect{
                val newItem = it.copy(learningStatus = newState.name)
                learningItemDao.insertLearningItem(newItem)
            }
        }

    }


}