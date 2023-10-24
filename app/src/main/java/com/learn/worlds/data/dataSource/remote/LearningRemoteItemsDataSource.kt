package com.learn.worlds.data.dataSource.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.Logger
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class LearningRemoteItemsDataSource @Inject constructor(
    private val authService: AuthService
) {

    val database by lazy { Firebase.database }
    var databaseRef: DatabaseReference? = null

    suspend fun fetchDataFromNetwork() = callbackFlow<Result<List<LearningItemAPI>>> {
        val dbListener = object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.e(message = "${error.message} :${error.code}")
                this@callbackFlow.trySendBlocking(Result.Error())
                close(Throwable(message = "${error.message} :${error.code}"))
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                this@callbackFlow.trySendBlocking(Result.Success(dataSnapshot.getValue<List<LearningItemAPI>>()?.filterNotNull() ?: listOf()))
                close()
            }
        }

        if (!authService.isAuthentificated()){
            this@callbackFlow.trySendBlocking(Result.Error())
            close()
        } else {
            databaseRef = database.getReference(authService.getUserUUID()!!)
            databaseRef?.addListenerForSingleValueEvent(dbListener)
        }
        awaitClose {
            databaseRef?.removeEventListener(dbListener)
        }

    }

    // todo переписать ошибка не будет обрабатываться
    suspend fun addLearningItems(learningItemAPI: List<LearningItemAPI>) = flow<Result<Result<LearningItemAPI>>> {
        Timber.e("addLearningItem: LearningItemAPI $learningItemAPI")
        try {
            databaseRef!!.setValue(learningItemAPI).addOnCompleteListener {
               Timber.d("addLearningItem:  success ${it.isSuccessful} errorMessage: ${it.exception?.localizedMessage}")
            }
            emit(Result.Complete)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }


}