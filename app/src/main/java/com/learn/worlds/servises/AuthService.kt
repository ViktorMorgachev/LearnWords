package com.learn.worlds.servises

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher, private val preferences: MySharedPreferences){
    private val auth by lazy { Firebase.auth }
    private val scope: CoroutineScope = CoroutineScope(dispatcher)
    fun isAuthentificated(): Boolean{
        return auth.currentUser != null
    }

    fun signIn(password: String, email: String){
        scope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener{ task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        Timber.d("signIn: success")
                    } else {
                        Timber.e(task.exception)
                    }
                }
        }
    }

    fun signUp(password: String, email: String) {
        scope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task->
                    if (task.isSuccessful) {
                        val user = auth.currentUser

                        Timber.d("")
                    } else {
                        Timber.e(task.exception)
                    }
                }
        }
    }

}