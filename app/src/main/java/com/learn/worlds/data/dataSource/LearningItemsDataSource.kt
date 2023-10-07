package com.learn.worlds.data.dataSource


import com.learn.worlds.data.model.db.LearningItemDB
import kotlinx.coroutines.flow.Flow

interface LearningItemsDataSource {
    val learningItems: Flow<List<LearningItemDB>>
    suspend fun changeState(newState: String,  learningItemID: Int)

    suspend fun addLearningItem(learningItemDB: LearningItemDB)
}