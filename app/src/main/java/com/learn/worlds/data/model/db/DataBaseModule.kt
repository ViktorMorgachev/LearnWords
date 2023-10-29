package com.learn.worlds.data.model.db

import android.content.Context
import androidx.room.Room
import com.learn.worlds.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideLearningDao(appDatabase: AppDatabase): LearningItemDao {
        return appDatabase.learningItemsDao()
    }

    // todo change soon AppDatabase name for package name (for so happy naming)
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "AppDatabase"
        ).build()
    }
}