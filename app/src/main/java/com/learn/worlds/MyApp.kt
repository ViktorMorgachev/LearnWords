package com.learn.worlds

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.DelegatingWorkerFactory
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Logger
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.remote.SynchronizationWorker
import com.learn.worlds.di.SynchronizationWorkerFactory
import com.learn.worlds.utils.uniqueSyncronizationUniqueWorkName
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class MyApp : Application(), Configuration.Provider {

    @Inject lateinit var preferences: MySharedPreferences
    @Inject lateinit var hiltWorkerFactory: HiltWorkerFactory
    @Inject lateinit var learnItemsUseCase: LearnItemsUseCase

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        FirebaseApp.initializeApp(this)
        Firebase.database.setLogLevel(Logger.Level.DEBUG)
        FirebaseDatabase.getInstance()
        runPeriodicallySynchronization(Firebase.auth.currentUser != null)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        val myWorkerFactory = DelegatingWorkerFactory()
        myWorkerFactory.addFactory(SynchronizationWorkerFactory(learnItemsUseCase))
        return Configuration.Builder()
            .setWorkerFactory(myWorkerFactory)
            .setMinimumLoggingLevel(Log.INFO)
            .build()
    }

    private fun runPeriodicallySynchronization(isAuthentificated: Boolean) {
        val workManager = WorkManager.getInstance(this)
        if (!isAuthentificated) {
            workManager.cancelUniqueWork(uniqueSyncronizationUniqueWorkName)
            return
        }
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()
        val workRequest = PeriodicWorkRequestBuilder<SynchronizationWorker>(  30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()


        workManager.enqueueUniquePeriodicWork(
            uniqueSyncronizationUniqueWorkName,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}