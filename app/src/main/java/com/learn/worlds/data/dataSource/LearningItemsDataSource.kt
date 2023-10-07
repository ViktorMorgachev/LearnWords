package com.learn.worlds.data.dataSource

import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.db.LearningItemDB
import kotlinx.coroutines.flow.Flow

interface LearningItemsDataSource {
    val learningItems: Flow<List<LearningItemDB>>
    suspend fun changeState(newState: LearningStatus,  learningItemID: Int)
}