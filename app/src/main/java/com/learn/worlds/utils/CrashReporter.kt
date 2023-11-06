package com.learn.worlds.utils

import android.util.Log
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.learn.worlds.servises.AuthService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class CrashReporter @Inject constructor(private val authService: AuthService): Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        if (priority == Log.ERROR){
            Firebase.crashlytics.setUserId(authService.getUserUUID() ?: "not_registered_user")

            if (t != null){
                Firebase.crashlytics.recordException(t)
            } else {
                if (message.isNotEmpty() ){
                    Firebase.crashlytics.log(message)
                }
            }
        }

    }
}