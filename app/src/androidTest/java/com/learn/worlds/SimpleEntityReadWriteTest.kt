package com.learn.worlds

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.learn.worlds.data.local.AppDatabase
import com.learn.worlds.data.model.base.LearningStatus
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
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
    fun writeUserAndReadInList()  {
        val data = LearningItemDB(nativeData = "девушка", foreignData = "girl", timeStampUIID = 1657977381214)
        userDao.insertLearningItemTest(data)
        var items = userDao.getLearningItemsTest()
        assertThat(items.size, equalTo(1))
    }

    @Test
    @Throws(Exception::class)
    fun writeAndChangeDataAfter()  {
        val data = LearningItemDB(nativeData = "котёнок", foreignData = "kitty", timeStampUIID = 1657977381212)
        userDao.insertLearningItemTest(data)
        var items = userDao.getLearningItemsTest()
        userDao.updateLearningItemTest(items.first { it.nativeData == "котёнок" }.copy(
            learningStatus = LearningStatus.LEARNED.name
        ))
        items = userDao.getLearningItemsTest()
        assertThat(items.first { it.nativeData == "котёнок" }.learningStatus, equalTo(LearningStatus.LEARNED.name))
    }
}