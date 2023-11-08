package com.learn.worlds.servises

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume



@Singleton
class FirebaseAuthService @Inject constructor(
    private val firebaseAuthErrorWrapper: FirebaseAuthErrorWrapper
) {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val auth by lazy { Firebase.auth }
    init {
        auth.setLanguageCode("ru")
    }

    private val _authState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val authState = _authState.asStateFlow()

    init {
        scope.launch {
            _authState.emit(isAuthentificated())
        }
    }
    fun isAuthentificated(): Boolean {
        return getUserUUID() != null
    }

    fun getUserUUID(): String? {
        return auth.currentUser?.uid
    }

    suspend fun signIn(password: String, email: String): Result<Any> {
        return suspendCancellableCoroutine { continuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    scope.launch {
                        continuation.resume(Result.Complete)
                        _authState.emit(true)
                    }

                }.addOnFailureListener {
                    scope.launch {
                        _authState.emit(false)
                        continuation.resume(Result.Error(firebaseAuthErrorWrapper.getActualErrorText(it)))
                    }
                }
        }

    }

    suspend fun signUp(password: String, email: String):  Result<Any> {
        return suspendCancellableCoroutine { continuation ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    scope.launch {
                        continuation.resume(Result.Complete)
                        _authState.emit(true)
                    }

                }.addOnFailureListener {
                    scope.launch {
                        _authState.emit(false)
                        continuation.resume(Result.Error(firebaseAuthErrorWrapper.getActualErrorText(it)))
                    }
                }
        }
    }

}

