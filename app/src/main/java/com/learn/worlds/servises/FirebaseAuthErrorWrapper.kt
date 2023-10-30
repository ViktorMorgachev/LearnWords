package com.learn.worlds.servises

import android.content.Context
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuthException
import com.learn.worlds.utils.ErrorType
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthErrorWrapper @Inject constructor(@ApplicationContext val context: Context) {

    private enum class FirebaseErrorType {
        ERROR_USER_DISABLED, INVALID_LOGIN_CREDENTIALS
    }

    fun getActualErrorText(throwable: Throwable): ErrorType {
        Timber.e(throwable)
        if (throwable is FirebaseAuthException){
            val errorCode = (throwable as FirebaseAuthException).errorCode
            if (errorCode == FirebaseErrorType.ERROR_USER_DISABLED.name) {
                return ErrorType.ERROR_USER_DISABLED
            }
        }
        if (throwable is FirebaseException){
            if ((throwable as FirebaseException).message?.contains(FirebaseErrorType.INVALID_LOGIN_CREDENTIALS.name) == true){
                return ErrorType.INVALID_LOGIN_CREDENTIALS
            }
        }
        if (throwable.message?.contains("Try again later") == true){
            return ErrorType.TRY_LATER
        }
        return ErrorType.SOMETHING_ERROR
    }
}