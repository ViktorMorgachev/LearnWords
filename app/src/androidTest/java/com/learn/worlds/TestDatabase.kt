package com.learn.worlds

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.worlds.data.local.AppDatabase
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
    private lateinit var userDao: LearningItemDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java).build()
        userDao = db.learningItemsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    suspend fun writeUserAndReadInList() = runBlocking{
        val data = LearningItemDB(nativeData = "котёнок", foreignData = "kitty")
        userDao.insertLearningItem(data)
        userDao.getLearningItems().collectLatest {
            assertThat(it[0], equalTo(data))
        }

    }
}