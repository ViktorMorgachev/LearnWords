package com.learn.worlds.servises

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine



@Singleton
class AuthService @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthErrorWrapper: FirebaseAuthErrorWrapper
) {
    private val auth by lazy { Firebase.auth }

    init {
        auth.setLanguageCode("ru")
    }

    fun isAuthentificated(): Boolean {
        return auth.currentUser != null
    }

    fun getUserUUID(): String? {
        return auth.currentUser?.uid
    }

    suspend fun signIn(password: String, email: String): Result<Any> {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result.user != null) {
                        continuation.resume(Result.Complete)
                    } else {
                        continuation.resume(Result.Error(firebaseAuthErrorWrapper.getActualErrorText(it.exception?.localizedMessage)))
                    }
                }
        }

    }

    suspend fun signUp(password: String, email: String):  Result<Any> {
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful && it.result.user != null) {
                        continuation.resume(Result.Complete)
                    } else {
                        continuation.resume(Result.Error(firebaseAuthErrorWrapper.getActualErrorText(it.exception?.localizedMessage)))
                    }
                }
        }
    }

}


