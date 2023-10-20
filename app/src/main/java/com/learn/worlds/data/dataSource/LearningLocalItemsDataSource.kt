package com.learn.worlds.data.dataSource

import android.content.Context
import com.learn.worlds.R
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.db.LearningItemDao
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class LearningLocalItemsDataSource @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, @ApplicationContext val context: Context, private val mySharedPreferences: MySharedPreferences, private val learningItemDao: LearningItemDao):
    LearningItemsDataSource {

    override val learningItems:  Flow<List<LearningItemDB>>  = learningItemDao.getLearningItems()

    override suspend fun changeState(newState: String, learningItemID: Int) = flow{
        Timber.e("changeState: newState $newState learningItemID: $learningItemID")
        learningItemDao.getLearningItem(learningItemID).collect{
            emit(Result.Loading)
            try {
                emit(Result.Loading)
                val newItem = it.copy(learningStatus = newState)
                learningItemDao.insertLearningItem(newItem)
                emit(Result.Complete)
            } catch (t: Throwable){
                Timber.e(t)
                emit(Result.Error())
            }
        }

    }

    override suspend fun addLearningItem(learningItemDB: LearningItemDB)  = flow {
        Timber.e("addLearningItem: learningItemDB $learningItemDB")
        if (mySharedPreferences.dataBaseLocked){
            emit(Result.Error(context.getString(R.string.error_limits_adding_words)))
        } else {
            try {
                learningItemDao.insertLearningItem(learningItemDB)
                emit(Result.Complete)
            } catch (t: Throwable){
                Timber.e(t)
                emit(Result.Error())
            }
        }
    }.flowOn(dispatcher)

}