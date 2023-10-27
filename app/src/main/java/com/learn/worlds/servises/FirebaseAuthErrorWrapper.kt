package com.learn.worlds.servises

import android.content.Context
import com.learn.worlds.R
import com.learn.worlds.utils.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthErrorWrapper @Inject constructor(@ApplicationContext val context: Context){
    fun getActualErrorText(firebaseAuthError: String?): ErrorType{
        if (firebaseAuthError == null) return ErrorType.SOMETHING_ERROR
        if (firebaseAuthError.contains("INVALID_LOGIN_CREDENTIALS")){
            return ErrorType.INVALID_FIREBASE_LOGIN_CREDENTIALS
        } else return ErrorType.SOMETHING_ERROR
    }
}