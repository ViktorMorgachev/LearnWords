package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.LearningLocalItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LearningStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(private val learningItemsLocalDataSource: LearningLocalItemsDataSource) {

    val data: Flow<List<LearningItem>> =
        learningItemsLocalDataSource.learningItems.transform { initialData -> initialData.map { it.toLearningItem() } }

    suspend fun changeState(newState: LearningStatus, itemID: Int) {
        learningItemsLocalDataSource.changeState(newState = newState.name, learningItemID = itemID)
    }

    suspend fun addLearningItem(learningItem: LearningItem) {
        learningItemsLocalDataSource.addLearningItem(learningItem.toLearningItemDB())
    }

}