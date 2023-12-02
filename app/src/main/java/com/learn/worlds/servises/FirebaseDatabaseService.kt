package com.learn.worlds.servises

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDatabaseService @Inject constructor(){
    val database by lazy { Firebase.database }
    fun getDatabaseRef(databaseRef: String?): DatabaseReference? {
        databaseRef?.let {
           return  database.getReference(it)
        }
        return null
    }
}