package com.learn.worlds.data.dataSource.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume

class LearningRemoteItemsDataSource @Inject constructor(
    private val authService: AuthService,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
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
                this@callbackFlow.trySendBlocking(
                    Result.Success(
                        dataSnapshot.getValue<List<LearningItemAPI>>()?.filterNotNull() ?: listOf()
                    )
                )
                close()
            }
        }

        if (!authService.isAuthentificated()) {
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

    suspend fun addLearningItems(learningItemAPI: List<LearningItemAPI>) =
        suspendCancellableCoroutine<Result<List<LearningItem>>> { cancellableContinuation ->
            cancellableContinuation.invokeOnCancellation {
                cancellableContinuation.cancel(it)
            }
            if (databaseRef == null) {
                authService.getUserUUID()?.let {
                    databaseRef = database.getReference(it)
                }
            }
            if (databaseRef != null) {
                databaseRef!!.setValue(learningItemAPI).addOnCompleteListener {
                    if (it.isSuccessful) {
                        cancellableContinuation.resume(Result.Complete)
                    } else {
                        Timber.e(it.exception)
                        cancellableContinuation.resume(Result.Error())
                    }
                }
            } else {
                Timber.e("databaseRef is nullable maybe token expired please check")
                cancellableContinuation.resume(Result.Error())
            }
        }

}