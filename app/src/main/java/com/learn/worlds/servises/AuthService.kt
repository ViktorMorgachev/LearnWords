package com.learn.worlds.servises

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.learn.worlds.utils.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume



@Singleton
class AuthService @Inject constructor(
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


