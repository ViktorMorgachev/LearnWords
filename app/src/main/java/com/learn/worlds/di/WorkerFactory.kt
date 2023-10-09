package com.learn.worlds.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.remote.SynchronizationWorker
import com.learn.worlds.data.repository.LearningItemsRepository
import kotlinx.coroutines.CoroutineDispatcher

class SynchronizationWorkerFactory (private val ioDispather: CoroutineDispatcher,
                                    private val learningItemsRepository: LearnItemsUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): CoroutineWorker {
        return SynchronizationWorker(appContext, workerParameters, ioDispather, learningItemsRepository)

    }
}