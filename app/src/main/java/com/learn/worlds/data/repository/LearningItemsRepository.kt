package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.LearningLocalItemsDataSource
import javax.inject.Inject

class LearningItemsRepository @Inject constructor(private val learningItemsLocalDataSource: LearningLocalItemsDataSource) {

    suspend fun getAllLearningItems(){
        learningItemsLocalDataSource.learningItems
    }

    suspend fun updateLearningItem(){

    }

}