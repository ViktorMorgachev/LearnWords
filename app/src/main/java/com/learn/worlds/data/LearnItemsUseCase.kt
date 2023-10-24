package com.learn.worlds.data

import android.content.Context
import com.learn.worlds.R
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val preferences: MySharedPreferences,
    private val learningItemsRepository: LearningItemsRepository
) {

    val actualData = learningItemsRepository.data
    suspend fun addLearningItem(learningItem: LearningItem) = flow {
        Timber.e("learningItem $learningItem")
        if (preferences.dataBaseLocked) {
            emit(Result.Error(context.getString(R.string.error_limits_adding_words)))
        } else {
            try {
                learningItemsRepository.writeToLocalDatabase(learningItem).collect()
                emit(Result.Complete)
            } catch (t: Throwable) {
                Timber.e(t)
                emit(Result.Error())
            }
        }
    }.flowOn(dispatcher)

    suspend fun syncItemsFromNetwork() = learningItemsRepository.writeToLocalDatabase(learningItemsRepository.fetchDataFromNetwork())


}