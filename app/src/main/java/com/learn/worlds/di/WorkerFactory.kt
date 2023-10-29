package com.learn.worlds.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.remote.SynchronizationWorker

class SynchronizationWorkerFactory (private val learningItemsUseCase: LearnItemsUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): CoroutineWorker {
        return SynchronizationWorker(appContext, workerParameters, learningItemsUseCase)

    }
}