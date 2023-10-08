package com.learn.worlds.data.dataSource.di

import com.learn.worlds.data.dataSource.LearningItemsDataSource
import com.learn.worlds.data.dataSource.LearningLocalItemsDataSource
import com.learn.worlds.data.model.db.LearningItemDao
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Singleton
    @Binds
    fun bindsLearningLocalDataSource(
         learningItemsDataSource: LearningLocalItemsDataSource
    ): LearningItemsDataSource
}