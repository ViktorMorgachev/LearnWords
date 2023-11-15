package com.learn.worlds

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.worlds.data.local.AppDatabase
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException



@RunWith(AndroidJUnit4::class)
class SimpleEntityReadWriteTest {
   private  lateinit var userDao: LearningItemDao
   private  lateinit var db: AppDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        userDao = db.learningItemsDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun writeUserAndReadInList() = runBlocking  {
        val data = LearningItemDB(nativeData = "девушка", foreignData = "girl", timeStampUIID = 1657977381214)
        userDao.insertLearningItem(data)
        var items = userDao.getLearningItems()
        assertThat(items.size, equalTo(1))
    }

    @Test
    @Throws(Exception::class)
    fun writeAndChangeDataAfter()  = runBlocking {
        val data = LearningItemDB(nativeData = "котёнок", foreignData = "kitty", timeStampUIID = 1657977381212)
        userDao.insertLearningItem(data)
        var items = userDao.getLearningItems()
        userDao.updateLearningItem(items.first { it.nativeData == "котёнок" }.copy(
            learningStatus = LearningStatus.LEARNED.name
        ))
        items = userDao.getLearningItems()
        assertThat(items.first { it.nativeData == "котёнок" }.learningStatus, equalTo(LearningStatus.LEARNED.name))
    }
}