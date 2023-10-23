package com.learn.worlds.servises

import android.content.Context
import com.learn.worlds.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthErrorWrapper @Inject constructor(@ApplicationContext val context: Context){
    fun getActualErrorText(firebaseAuthError: String?): String?{
        if (firebaseAuthError == null) return null
        if (firebaseAuthError.contains("INVALID_LOGIN_CREDENTIALS")){
            return context.getString(R.string.error_invalid_login)
        } else return firebaseAuthError
    }
}