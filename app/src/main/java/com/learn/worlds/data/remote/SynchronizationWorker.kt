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
import kotlinx.coroutines.flow.flowOn

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @IoDispatcher private val ioDispather: CoroutineDispatcher,
    private val learnItemsUseCase: LearnItemsUseCase,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        learnItemsUseCase.syncItems().flowOn(ioDispather)
        return Result.success()
    }
}