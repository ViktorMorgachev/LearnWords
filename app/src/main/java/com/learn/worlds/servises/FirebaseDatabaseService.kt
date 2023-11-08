package com.learn.worlds.servises

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDatabaseService @Inject constructor(){
    val database by lazy { Firebase.database }
    var databaseRef: DatabaseReference? = null

    fun getDatabaseRef(userUUID: String?): DatabaseReference? {
        if (databaseRef == null){
            userUUID?.let {
                databaseRef = database.getReference(it)
            }
        }
        return databaseRef

    }
}