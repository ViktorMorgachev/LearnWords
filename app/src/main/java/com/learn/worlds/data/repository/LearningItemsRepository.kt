package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.LearningItemsDataSource
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.di.MockDataSource
import com.learn.worlds.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(@MockDataSource private val learningItemsLocalDataSource: LearningItemsDataSource) {

    val data: Flow<Result<List<LearningItem>>> = learningItemsLocalDataSource.learningItems.map { initialData ->
            try {
                Result.Success(initialData.map { it.toLearningItem() })
            } catch (t: Throwable) {
                Result.Error()
            }
        }

    suspend fun changeState(newState: String, itemID: Int) =  learningItemsLocalDataSource.changeState(newState = newState, learningItemID = itemID)

    suspend fun addLearningItem(learningItem: LearningItem) = learningItemsLocalDataSource.addLearningItem(learningItem.toLearningItemDB())

}