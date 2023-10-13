package com.learn.worlds.data.dataSource.di

import com.learn.worlds.data.dataSource.LearningItemsDataSource
import com.learn.worlds.data.dataSource.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.mockDataSource.di.MockLearningLocalItemsDataSource
import com.learn.worlds.di.MockDataSource
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

    @Singleton
    @MockDataSource
    @Binds
    fun bindsMockLearningLocalDataSource(
        learningItemsDataSource: MockLearningLocalItemsDataSource
    ): LearningItemsDataSource
}