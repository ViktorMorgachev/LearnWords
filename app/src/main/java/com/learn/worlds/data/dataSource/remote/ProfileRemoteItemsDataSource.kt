package com.learn.worlds.data.dataSource.remote


import android.content.Context
import com.google.firebase.database.getValue
import com.learn.worlds.data.model.remote.ProfileAPI
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


class ProfileRemoteItemsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseDatabaseService: FirebaseDatabaseService,
    private val firebaseAuthService: FirebaseAuthService,
) {

    suspend fun addProfileData(profileAPI: ProfileAPI) = suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (!firebaseAuthService.isAuthentificated()) {
                cancellableContinuation.safeResume(Result.Error(ErrorType.NOT_AUTHENTICATED))
            }
            val databaseRef = firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
            if (databaseRef != null) {
                databaseRef.child(FirebaseDatabaseChild.PROFILE.path).setValue(profileAPI)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            databaseRef.child(FirebaseDatabaseChild.PROFILE_LAST_SYNC_DATETIME.path)
                                .setValue(getCurrentDateTime()).addOnCompleteListener {
                                    if (it.isSuccessful) {
                                        cancellableContinuation.safeResume(Result.Complete)
                                    } else {
                                        Timber.e(it.exception, "add to remote datetime")
                                        cancellableContinuation.safeResume(Result.Error())
                                    }
                                }
                        } else {
                            Timber.e(it.exception, "add to remote profile item")
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

    suspend fun fetchProfileData() = callbackFlow<Result<ProfileAPI?>> {
        if (!firebaseAuthService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            val databaseRef = firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
            if (databaseRef != null) {
                databaseRef.get().addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result != null) {
                                if (!it.result.exists()) {
                                    trySendBlocking(Result.Success(null))
                                    close()
                                } else {
                                    var result = it.result.children.firstOrNull { it.key == FirebaseDatabaseChild.PROFILE.path }?.getValue<ProfileAPI>()
                                    trySendBlocking(Result.Success(result))
                                    close()
                                }
                            } else {
                                trySendBlocking(Result.Success(null))
                                close()
                            }
                        } else {
                            Timber.e(it.exception, "fetch from remote learning items")
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


    suspend fun replaceProfileItem(profileAPI: ProfileAPI) = suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (!firebaseAuthService.isAuthentificated()) {
                cancellableContinuation.safeResume(Result.Error(ErrorType.NOT_AUTHENTICATED))
            }
            val databaseRef = firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
            if (databaseRef != null) {
                databaseRef.updateChildren(mapOf<String, ProfileAPI>(FirebaseDatabaseChild.PROFILE.path to profileAPI))
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            cancellableContinuation.safeResume(Result.Complete)
                        } else {
                            Timber.e(it.exception, "updated profile item")
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