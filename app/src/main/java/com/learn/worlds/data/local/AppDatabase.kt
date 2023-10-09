package com.learn.worlds.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao

@Database(entities = [LearningItemDB::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun learningItemsDao(): LearningItemDao
}