package com.learn.worlds.data.dataSource.remote


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.firebase.database.getValue
import com.google.firebase.storage.StorageException
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.request.SpellingCheckRequest
import com.learn.worlds.data.model.remote.request.TextToSpeechRequest
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenSpellCheckResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse
import com.learn.worlds.data.remote.ApiService
import com.learn.worlds.data.remote.ai.ImageFileNameUtils
import com.learn.worlds.data.remote.ai.SpeechFileNameUtils
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.servises.FirebaseDatabaseService
import com.learn.worlds.servises.FirebaseStorageService
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.FirebaseDatabaseChild
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.getCurrentDateTime
import com.learn.worlds.utils.isImage
import com.learn.worlds.utils.isMp3File
import com.learn.worlds.utils.safeResume
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject


class LearningRemoteItemsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val firebaseDatabaseService: FirebaseDatabaseService,
    private val firebaseAuthService: FirebaseAuthService,
    private val firebaseStorageService: FirebaseStorageService,
    private val apiService: ApiService,
    private val ktorHttpClient: HttpClient
) {

    suspend fun uploadTextSpeechToFirebase(file: File) = callbackFlow<Result<Nothing>> {
        if (!firebaseAuthService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            val firebaseStorageRef = firebaseStorageService.getStorageRef(
                path = SpeechFileNameUtils.getPathForFirebaseStorage(file)
            )
            Timber.d("firebaseStorageRef: $firebaseStorageRef")
            val bytes = file.readBytes()
            val uploadTask = firebaseStorageRef.putBytes(bytes)
            uploadTask.addOnSuccessListener {
                this@callbackFlow.trySendBlocking(Result.Complete)
                close()
            }.addOnFailureListener {
                Timber.e(it)
                this@callbackFlow.trySendBlocking(Result.Error())
                close()
            }
            close()
        }
        awaitClose {
            close()
        }
    }

    suspend fun uploadImageToFirebase(file: File) = callbackFlow<Result<Nothing>> {
        if (!firebaseAuthService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            val firebaseStorageRef = firebaseStorageService.getStorageRef(
                path = ImageFileNameUtils.getPathForFirebaseStorage(file)
            )
            Timber.d("firebaseStorageRef: $firebaseStorageRef")
            val bytes = file.readBytes()
            val uploadTask = firebaseStorageRef.putBytes(bytes)
            uploadTask.addOnSuccessListener {
                this@callbackFlow.trySendBlocking(Result.Complete)
                close()
            }.addOnFailureListener {
                Timber.e(it)
                this@callbackFlow.trySendBlocking(Result.Error())
                close()
            }
        }
        awaitClose {
            close()
        }
    }

    suspend fun downloadImageFromFirebase(imageGeneration: ImageGeneration) =
        callbackFlow<Result<Nothing>> {
            if (!firebaseAuthService.isAuthentificated()) {
                this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
                close()
            } else {
                val firebaseStorageRef = firebaseStorageService.getStorageRef(
                    path = ImageFileNameUtils.getPathForFirebaseStorage(imageGeneration.file)
                )
                Timber.d("firebaseStorageRef: $firebaseStorageRef")
                firebaseStorageRef.getFile(imageGeneration.file)
                    .addOnSuccessListener {
                        this@callbackFlow.trySendBlocking(Result.Complete)
                        close()
                    }.addOnFailureListener {
                        if (it is StorageException) {
                            if ((it as StorageException).httpResultCode == 404) {
                                this@callbackFlow.trySendBlocking(Result.Complete)
                                close()
                            } else {
                                Timber.e(it)
                                this@callbackFlow.trySendBlocking(Result.Error())
                                close()
                            }
                        } else {
                            Timber.e(it)
                            this@callbackFlow.trySendBlocking(Result.Error())
                            close()
                        }
                    }
            }
            awaitClose {
                close()
            }
        }

    suspend fun downloadTextsSpeechFromFirebase(textToSpeech: TextToSpeech) = callbackFlow<Result<Nothing>> {
            if (!firebaseAuthService.isAuthentificated()) {
                this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
                close()
            } else {
                val firebaseStorageRef = firebaseStorageService.getStorageRef(
                    path = SpeechFileNameUtils.getPathForFirebaseStorage(textToSpeech.file)
                )
                Timber.d("firebaseStorageRef: $firebaseStorageRef")
                firebaseStorageRef.getFile(textToSpeech.file)
                    .addOnSuccessListener {
                        this@callbackFlow.trySendBlocking(Result.Complete)
                        close()
                    }.addOnFailureListener {
                        if (it is StorageException) {
                            if ((it as StorageException).httpResultCode == 404) {
                                this@callbackFlow.trySendBlocking(Result.Complete)
                                close()
                            } else {
                                Timber.e(it)
                                this@callbackFlow.trySendBlocking(Result.Error())
                                close()
                            }
                        } else {
                            Timber.e(it)
                            this@callbackFlow.trySendBlocking(Result.Error())
                            close()
                        }
                    }
            }
            awaitClose {
                close()
            }
        }

    suspend fun getTextsSpeechFromApi(textToSpeech: TextToSpeech) = flow<Result<EidenTextToSpeechResponse>> {
            val languageApi = SpeechFileNameUtils.getLanguageForApi(textToSpeech.file)
            // Additional Protection
            if (textToSpeech.file.isMp3File()) {
                emit(Result.Error())
                return@flow
            }
            try {
                val result = apiService.getTextsSpeech(
                    textToSpeechResponseGson = TextToSpeechRequest(
                        text = textToSpeech.file.name.substringBefore(".").substringAfter("_"),
                        language = languageApi.desc
                    )
                )
                emit(Result.Success(result))
            } catch (t: Throwable){
                Timber.e(t)
                emit(Result.Error())
            }

        }.flowOn(dispatcher)


    suspend fun spellingCheck(spellTextCheck: SpellTextCheck) = flow<Result<EidenSpellCheckResponse>> {
        try {
            val result =  apiService.spellCheck(spellingCheckRequest = SpellingCheckRequest(text = spellTextCheck.requestText))
            emit(Result.Success(result))
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }

    }.flowOn(dispatcher)

    suspend fun getImageFromApi(imageGeneration: ImageGeneration) = flow<Result<EidenImageGenerationResponse>> {
            if (imageGeneration.file.isImage()) {
                emit(Result.Error())
                return@flow
            }
            try {
                val result =  apiService.getImageGeneration(
                    imageGenerationRequest = ImageGenerationRequest(
                        text = imageGeneration.file.name.substringBefore(".") + ", icon"))
                emit(Result.Success(result))
            } catch (t: Throwable){
                Timber.e(t)
                emit(Result.Error())
            }

        }.flowOn(dispatcher)


    private fun saveFile(data: ByteArray, file: File) {
        FileOutputStream(file).use {
            it.write(data)
        }
    }

    private fun initFile(name: String): File {
        var file = File(context.cacheDir, name)
        if (file.exists()) {
            file.delete()
            file = File(context.cacheDir, name)
        }
        return file
    }

    suspend fun downloadTextSpeechFromApi(textToSpeech: TextToSpeech) = flow<Result<TextToSpeech>> {
        val response = ktorHttpClient.get(textToSpeech.actualFileUrl!!)
        if (response.status == HttpStatusCode.OK) {
            val file = initFile(textToSpeech.file.name)
            saveFile(data = response.readBytes(), file = file)
            emit(Result.Success(textToSpeech.copy(file = file)))
        } else {
            Timber.e("Status: ${response.status} RequestTime: ${response.requestTime}")
            emit(Result.Error())
        }
    }.flowOn(dispatcher)



    suspend fun downloadImageFromApi(imageGeneration: ImageGeneration) =
        flow<Result<ImageGeneration>> {
            val response = ktorHttpClient.get(imageGeneration.actualFileUrl!!)
            if (response.status == HttpStatusCode.OK) {
                val file = initFile(imageGeneration.file.name)
                val fileCompressed = initFile("compressed_${imageGeneration.file.name}")
                saveFile(data = response.readBytes(), file = file)
                if (file.isImage()){
                    compressImageFile(file, fileCompressed)
                    fileCompressed.renameTo(file)
                }
                emit(Result.Success(imageGeneration.copy(file = file)))
            } else {
                Timber.e("Status: ${response.status} RequestTime: ${response.requestTime}")
                emit(Result.Error())
            }
        }.flowOn(dispatcher)

    private fun compressImageFile(fileFrom: File, fileTo: File) {
        val quality: Int = 80
        BitmapFactory.decodeFile(fileFrom.path)?.let { bitmap ->
            ByteArrayOutputStream().use { byteArrayOutputStream ->
                FileOutputStream(fileTo.path).use { fileOutputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)
                    fileOutputStream.write(byteArrayOutputStream.toByteArray())
                }
            }
        }
    }


    suspend fun fetchItemsIdsForRemoving() = callbackFlow<Result<List<Long>>> {
        if (!firebaseAuthService.isAuthentificated()) {
            this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
            close()
        } else {
            val databaseRef =
                firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID()!!)
            if (databaseRef != null) {
                databaseRef.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).get()
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            if (it.result != null) {
                                if (!it.result.exists()) {
                                    trySendBlocking(Result.Success(listOf()))
                                    close()
                                } else {
                                    var resultList = listOf<LearningItemAPI>()
                                    resultList =
                                        it.result.children.map { it.getValue<LearningItemAPI>() }
                                            .filterNotNull()
                                    trySendBlocking(Result.Success(resultList.filter { it.deletedStatus }
                                        .map { it.timeStampUIID }))
                                    close()

                                }
                            } else {
                                trySendBlocking(Result.Success(listOf()))
                                close()
                            }
                        } else {
                            Timber.e(it.exception, "fetch from remote learning deleted items")
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


    suspend fun fetchDataFromNetwork(ignoreRemovingItems: Boolean) = callbackFlow<Result<List<LearningItemAPI>>> {
            if (!firebaseAuthService.isAuthentificated()) {
                this@callbackFlow.trySendBlocking(Result.Error(ErrorType.NOT_AUTHENTICATED))
                close()
            } else {
                val databaseRef =
                    firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
                if (databaseRef != null) {
                    databaseRef.child(FirebaseDatabaseChild.LEARNING_ITEMS.path).get()
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                if (it.result != null) {
                                    if (!it.result.exists()) {
                                        trySendBlocking(Result.Success(listOf()))
                                        close()
                                    } else {
                                        var resultList = listOf<LearningItemAPI>()
                                        resultList = it.result.children.map { it.getValue<LearningItemAPI>() }.filterNotNull()
                                        if (ignoreRemovingItems) {
                                            trySendBlocking(Result.Success(resultList.filter { !it.deletedStatus }))
                                            close()
                                        } else {
                                            trySendBlocking(Result.Success(resultList))
                                            close()
                                        }
                                    }
                                } else {
                                    trySendBlocking(Result.Success(listOf()))
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

    suspend fun addLearningItem(learningItem: LearningItemAPI) =
        suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (!firebaseAuthService.isAuthentificated()) {
                cancellableContinuation.safeResume(Result.Error(ErrorType.NOT_AUTHENTICATED))
            }
            val databaseRef =
                firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
            if (databaseRef != null) {
                databaseRef.child(FirebaseDatabaseChild.LEARNING_ITEMS.path)
                    .child("${learningItem.timeStampUIID}").setValue(learningItem)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            databaseRef!!.child(FirebaseDatabaseChild.LEARNING_ITEMS_LAST_SYNC_DATETIME.path)
                                .setValue(getCurrentDateTime()).addOnCompleteListener {
                                    if (it.isSuccessful) {
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


    suspend fun replaceRemoteItems(learningItem: LearningItemAPI) =
        suspendCancellableCoroutine<Result<Nothing>> { cancellableContinuation ->
            if (!firebaseAuthService.isAuthentificated()) {
                cancellableContinuation.safeResume(Result.Error(ErrorType.NOT_AUTHENTICATED))
            }
            val databaseRef =
                firebaseDatabaseService.getDatabaseRef(firebaseAuthService.getUserUUID())
            if (databaseRef != null) {
                databaseRef.child(FirebaseDatabaseChild.LEARNING_ITEMS.path)
                    .updateChildren(mapOf<String, LearningItemAPI>("${learningItem.timeStampUIID}" to learningItem))
                    .addOnCompleteListener {
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