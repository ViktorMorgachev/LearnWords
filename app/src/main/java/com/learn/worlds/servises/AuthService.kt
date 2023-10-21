package com.learn.worlds.servises

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthService @Inject constructor(){
    private val auth by lazy { Firebase.auth }

    fun isAuthentificated(): Boolean{
        return auth.currentUser != null
    }



}