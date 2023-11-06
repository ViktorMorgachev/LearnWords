package com.learn.worlds.data.dataSource.remote


import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.servises.AuthService
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.FirebaseDatabaseChild
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.getCurrentDateTime
import com.learn.worlds.utils.safeResume
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject

class LearningRemoteItemsDataSource @Inject constructor(
    private val authService: AuthService
) {

    val database by lazy { Firebase.database }
    var databaseRef: DatabaseReference? = null

    suspend fun fetchItemsIdsForRemoving() = callbackFlow<Result<List<Long>>> {
        if (!authService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            databaseRef = database.getReference(authService.getUserUUID()!!)
            databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).get().addOnCompleteListener {
                if (it.isSuccessful){
                    if (it.result != null){
                        if (!it.result.exists()){
                            trySendBlocking(Result.Success(listOf()))
                        } else {
                            var resultList = listOf<LearningItemAPI>()
                            resultList =  it.result.children.map { it.getValue<LearningItemAPI>() }.filterNotNull()
                            trySendBlocking(Result.Success(resultList.filter { it.deletedStatus }.map { it.timeStampUIID }))

                        }
                    } else {
                        trySendBlocking(Result.Success(listOf()))
                    }
                    close()
                } else {
                    Timber.e(it.exception, "fetch from remote learning deleted items")
                    trySendBlocking(Result.Error())
                    close()
                }
            }
        }
        awaitClose {
          close()
        }

    }


    suspend fun fetchDataFromNetwork(ignoreRemovingItems: Boolean) = callbackFlow<Result<List<LearningItemAPI>>> {
        if (!authService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            databaseRef = database.getReference(authService.getUserUUID()!!)
            databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).get().addOnCompleteListener {
                if (it.isSuccessful){
                    if (it.result != null){
                        if (!it.result.exists()){
                            trySendBlocking(Result.Success(listOf()))
                        } else {
                            var resultList = listOf<LearningItemAPI>()
                            resultList =  it.result.children.map { it.getValue<LearningItemAPI>() }.filterNotNull()
                            if (ignoreRemovingItems){
                                trySendBlocking(Result.Success(resultList.filter { !it.deletedStatus }))
                            } else {
                                trySendBlocking(Result.Success(resultList))
                            }

                        }
                    } else {
                        trySendBlocking(Result.Success(listOf()))
                    }
                    close()
                } else {
                    Timber.e(it.exception, "fetch from remote learning items")
                    trySendBlocking(Result.Error())
                    close()
                }
            }
        }
        awaitClose {
            close()
        }

    }

    suspend fun addItem(learningItem: LearningItemAPI) =
        suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (databaseRef == null) {
                authService.getUserUUID()?.let {
                    databaseRef = database.getReference(it)
                }
            }
            if (databaseRef != null) {
                databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).child("${learningItem.timeStampUIID}").setValue(learningItem).addOnCompleteListener {
                    if (it.isSuccessful) {
                        databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS_LAST_SYNC_DATETIME.path).setValue(getCurrentDateTime()).addOnCompleteListener {
                            if (it.isSuccessful){
                                cancellableContinuation.safeResume(Result.Complete)
                            } else {
                                Timber.e(it.exception, "add to remote datetime")
                                cancellableContinuation.safeResume(Result.Error())
                            }
                        }
                    } else {
                        Timber.e(it.exception, "add to remote learning item")
                        cancellableContinuation.safeResume(Result.Error())
                    }
                }
            } else {
                Timber.e("database reference = $databaseRef maybe token expired please check")
                cancellableContinuation.safeResume(Result.Error())
            }
            cancellableContinuation.invokeOnCancellation {
                cancellableContinuation.cancel(it)
            }
        }


    suspend fun markItemsStatusRemoved(learningItemID: Long) =
        suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (databaseRef == null) {
                authService.getUserUUID()?.let {
                    databaseRef = database.getReference(it)
                }
            }
            if (databaseRef != null) {
                databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).child("$learningItemID").updateChildren(mapOf<String, Boolean>("deletedStatus" to true)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        cancellableContinuation.safeResume(Result.Complete)
                    } else {
                        Timber.e(it.exception, "mark items as deleted learning item")
                        cancellableContinuation.safeResume(Result.Error())
                    }
                }
            } else {
                Timber.e("database reference = $databaseRef maybe token expired please check")
                cancellableContinuation.safeResume(Result.Error())
            }
            cancellableContinuation.invokeOnCancellation {
                cancellableContinuation.cancel(it)
            }
        }

}