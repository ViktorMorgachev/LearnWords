package com.learn.worlds.data.dataSource.remote


import android.content.Context
import com.google.firebase.database.getValue
import com.learn.worlds.data.model.remote.DeviceIdAPI
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.servises.FirebaseDatabaseService
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.FirebaseDatabaseChild
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.getCurrentDateTime
import com.learn.worlds.utils.safeResume
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject


class AnonimBalanceDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseDatabaseService: FirebaseDatabaseService,
    private val firebaseAuthService: FirebaseAuthService,
) {

    suspend fun addDeviceID(deviceId: String) = suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
        if (!firebaseAuthService.isAuthentificated()) {
            cancellableContinuation.safeResume(Result.Error(ErrorType.NOT_AUTHENTICATED))
        }
        val databaseRef = firebaseDatabaseService.getDatabaseRef(FirebaseDatabaseChild.DEVICE_IDS_PATH.path)
        if (databaseRef != null) {
            databaseRef.child(deviceId).setValue(DeviceIdAPI(deviceId)).addOnCompleteListener {
                    if (it.isSuccessful) {
                        cancellableContinuation.safeResume(Result.Complete)
                    } else {
                        Timber.e(it.exception, "add to remote deviceID")
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


    suspend fun checkDeviceIDInDatabase(deviceId: String) = callbackFlow<Result<Boolean>> {
        if (!firebaseAuthService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            val databaseRef = firebaseDatabaseService.getDatabaseRef(FirebaseDatabaseChild.DEVICE_IDS_PATH.path)
            if (databaseRef != null) {
                databaseRef.get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result != null) {
                                if (!it.result.exists()) {
                                    trySendBlocking(Result.Success(false))
                                    close()
                                } else {
                                    var resultList = listOf<DeviceIdAPI>()
                                    resultList = it.result.children.map { it.getValue<DeviceIdAPI>() }.filterNotNull()
                                    trySendBlocking(Result.Success(resultList.map { it.device_id }.contains(deviceId)))
                                    close()
                                }
                            } else {
                                trySendBlocking(Result.Success(false))
                                close()
                            }
                        } else {
                            Timber.e(it.exception, "check remote deviceID")
                            trySendBlocking(Result.Error())
                            close()
                        }
                    }
            } else {
                Timber.e("database reference = $databaseRef maybe token expired please check")
                trySendBlocking(Result.Error())
                close()
            }
        }
        awaitClose {
            close()
        }

    }
}