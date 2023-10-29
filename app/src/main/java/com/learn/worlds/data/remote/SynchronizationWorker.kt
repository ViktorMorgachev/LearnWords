package com.learn.worlds.data.remote

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.di.IoDispatcher
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val learnItemsUseCase: LearnItemsUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Timber.d("SynchronizationWorker: doWork()")
        learnItemsUseCase.synckItems().collect{}
        return Result.success()
    }


}