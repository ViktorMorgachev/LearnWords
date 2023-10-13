package com.learn.worlds.data.dataSource


import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.Flow

interface LearningItemsDataSource {
    val learningItems: Flow<List<LearningItemDB>>
    suspend fun changeState(newState: String,  learningItemID: Int): Flow<Result<Any>>

    suspend fun addLearningItem(learningItemDB: LearningItemDB): Flow<Result<Any>>
}