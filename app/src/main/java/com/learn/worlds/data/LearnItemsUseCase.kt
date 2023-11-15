package com.learn.worlds.data


import android.content.Context
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.remote.CommonLanguage
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.prefs.SynckSharedPreferences
import com.learn.worlds.data.remote.ai.SpeechFileNameUtils
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.data.repository.LearningSynchronizationRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.isImage
import com.learn.worlds.utils.isMp3File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val firebaseAuthService: FirebaseAuthService,
    private val synkPreferences: SynckSharedPreferences,
    private val learningItemsRepository: LearningItemsRepository,
    private val learningSynchronizationRepository: LearningSynchronizationRepository
) {

    suspend fun actualData() = learningItemsRepository.data

    private val scope = CoroutineScope(dispatcher)

    suspend fun addLearningItem(learningItem: LearningItem) = flow<Result<Any>> {
        Timber.d("learningItem $learningItem")
        try {
            if (firebaseAuthService.isAuthentificated()) {
                learningItemsRepository.writeToRemoteDatabase(learningItem)
            }
            learningItemsRepository.writeToLocalDatabase(learningItem).collect {
                emit(it)
            }
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }

    suspend fun spellCheck(spellTextCheck: SpellTextCheck) = flow<Result<SpellTextCheck>> {
        var spellCheckActual: SpellTextCheck = spellTextCheck
        learningItemsRepository.spellCheck(spellTextCheck).collectLatest {
            if (it is Result.Success){
                if (it.data.suggestion?.lowercase() == spellTextCheck.requestText.lowercase()){
                    spellCheckActual = spellCheckActual.copy(suggestion = "")
                } else {
                    spellCheckActual = spellCheckActual.copy(suggestion = it.data.suggestion)
                }

            }
        }
        if (spellCheckActual.suggestion == null){
            emit(Result.Error(ErrorType.FAILED_TO_CHECK_SPELL_TEXT))
        } else {
            emit(Result.Success(spellCheckActual))
        }
    }

    suspend fun getImage(text: String) = flow<Result<String>> {
        val imageName = "${text}.jpg"
        var imageGeneration = ImageGeneration(file = File(context.cacheDir, imageName))
        val file = imageGeneration.file

        Timber.d("cardGeneration getImage: getLocalFile result: ${file.isImage()}")
        if (file.isImage()) {
            emit(Result.Success(imageName))
        } else {
            learningItemsRepository.getImageFromFirebase(imageGeneration).collectLatest {}
            Timber.d("cardGeneration getImage: getImageFromFirebase  result: ${file.isImage()}")
            if (file.isImage()){
                emit(Result.Success(imageName))
            } else {
                emit(Result.Error())
            }
            learningItemsRepository.getImageUrlFromApi(imageGeneration).collectLatest {
                if (it is Result.Success){
                    imageGeneration = imageGeneration.copy(actualFileUrl = it.data.actualFileUrl)
                }
            }
            Timber.d("cardGeneration getImage: getImageUrlFromApi  result: ${imageGeneration.actualFileUrl}")
            if (imageGeneration.actualFileUrl == null) {
                emit(Result.Error())
                return@flow
            }
            learningItemsRepository.loadImageFromApi(imageGeneration).collectLatest {}
            Timber.d("cardGeneration getImage: loadImageFromApi  result: ${imageGeneration.file.isImage()}")
            if (!imageGeneration.file.isImage()){
                emit(Result.Error())
            }
            learningItemsRepository.uploadImageToFirebase(imageGeneration).collectLatest {
                Timber.d("cardGeneration getImage: uploadImageToFirebase  result: ${it::class.java}")
                if (it is Result.Error){
                    synkPreferences.addImageNameForSync(imageName)
                }
            }
            emit(Result.Success(imageName))
        }
    }.flowOn(dispatcher)


    suspend fun getTextSpeech(text: String, language: CommonLanguage) = flow<Result<String>> {
        val mp3FileName = SpeechFileNameUtils.getFileNameForFirebaseStorage(language = language, name =  text)
        var textToSpeech = TextToSpeech(file = File(context.cacheDir, mp3FileName))
        val file = textToSpeech.file
        Timber.d("cardGeneration getTextSpeech: getLocalFile result: ${file.isMp3File()}")
        if (file.exists() && file.isMp3File()) {
            emit(Result.Success(mp3FileName))
        } else {
            learningItemsRepository.getTextsSpeechFromFirebase(textToSpeech).collectLatest {}
            Timber.d("cardGeneration getTextSpeech: getTextsSpeechFromFirebase  result: ${file.isMp3File()}")
            if (file.isMp3File()){
                emit(Result.Success(mp3FileName))
                return@flow
            }

            learningItemsRepository.getTextsSpeechUrlFromApi(textToSpeech).collectLatest {
                if (it is Result.Success){
                    textToSpeech = textToSpeech.copy(actualFileUrl = it.data.actualFileUrl)
                }
            }
            Timber.d("cardGeneration getTextSpeech: getTextsSpeechUrlFromApi  result: ${textToSpeech.actualFileUrl}")
            if (textToSpeech.actualFileUrl == null) {
                emit(Result.Error())
                return@flow
            }
            learningItemsRepository.loadFileSpeechFromApi(textToSpeech).collectLatest {}
            if (!textToSpeech.file.isMp3File()){
                emit(Result.Error())
            }
            Timber.d("cardGeneration getTextSpeech: loadFileSpeechFromApi  result: ${textToSpeech.file.isMp3File()}")
            learningItemsRepository.uploadTextSpeechToFirebase(textToSpeech).collectLatest {
                Timber.d("cardGeneration getTextSpeech: uploadTextSpeechToFirebase  result: ${it::class.java}")
                if (it is Result.Error){
                    synkPreferences.addMp3FileNameForSync(mp3FileName)
                }
            }
            emit(Result.Success(mp3FileName))
        }
    }.flowOn(dispatcher)

    /*1. Пишем данные из преференсов как есть заменяя данные в базе удалённой
    * 2. Чистим локальную базу, учитывая флаг deletedStatus
    * 3. После уже синхронизируем как обычно*/
    suspend fun synckItems() = flow<Result<List<LearningItem>>> {
        val remoteData = mutableListOf<LearningItem>()
        val itemsForRemoving = synkPreferences.getActualLearnItemsSynronization()
        var resultMarkedItems: Result<Nothing>? = null
        if (itemsForRemoving.isNotEmpty()) {
            learningSynchronizationRepository.replaceRemoteItems(itemsForRemoving).collectLatest {
                resultMarkedItems = it
            }
            if (resultMarkedItems == null || resultMarkedItems is Result.Error) {
                emit(Result.Error())
                return@flow
            }
        }
        synkPreferences.removeAllItemsIdsForSynshronization()

        scope.launch {
            val allMp3FileNames = synkPreferences.getActualMp3NamesItemsSynronization()
            if (allMp3FileNames.data.isNotEmpty()){
                learningSynchronizationRepository.uploadAllMp3Files(allMp3FileNames).collectLatest {}
            }
        }

        scope.launch {
            val allImagesForFirebase = synkPreferences.getActualImageNamesItemsSynronization()
            if (allImagesForFirebase.data.isNotEmpty()){
                learningSynchronizationRepository.uploadImageFiles(allImagesForFirebase).collectLatest {}
            }
        }

        scope.launch {
            var itemsRemovedIds = listOf<Long>()
            learningSynchronizationRepository.fetchItemsIdsForRemoving().collectLatest {
                if (it is Result.Success && it.data.isNotEmpty()) {
                    itemsRemovedIds = it.data
                }
            }
            if (itemsRemovedIds.isNotEmpty()) {
                learningItemsRepository.removeItemsFromLocalDatabase(itemsRemovedIds).collect {}
            }
        }


        learningItemsRepository.fetchDataFromNetwork(needIgnoreRemovingItems = true)
            .collectLatest {
                if (it is Result.Success) {
                    remoteData.addAll(it.data)
                } else {
                    remoteData.addAll(listOf())
                }
            }
        val localData = mutableListOf<LearningItem>()
        learningItemsRepository.getDataFromDatabase().collect {
            localData.addAll(it)
        }
        val dataForNetwork: MutableList<LearningItem> = mutableListOf()
        val dataForLocal: MutableList<LearningItem> = mutableListOf()
        localData.plus(remoteData).forEach {
            if (!localData.contains(it)) {
                dataForLocal.add(it)
            }
            if (!remoteData.contains(it)) {
                dataForNetwork.add(it)
            }
        }
        Timber.d(
            "dataForRemote: ${dataForNetwork.joinToString(", ")} " +
                    "\ndataForLocal: ${dataForLocal.joinToString(", ")}  "
        )

        val synckResult = mutableListOf<Result<Nothing>>()
        learningItemsRepository.writeListToLocalDatabase(dataForLocal).collect {
            synckResult.add(it)
        }
        learningItemsRepository.writeListToRemoteDatabase(dataForNetwork).collect {
            synckResult.add(it)
        }
        if (synckResult.any { it is Result.Error }) {
            emit(synckResult.first { it is Result.Error })
        } else {
            if (synckResult.any { it is Result.Complete }) {
                emit(Result.Success(listOf()))
            } else {
                emit(Result.Complete)
            }
        }
    }

    suspend fun deleteWordItem(learningItem: LearningItem) = flow<Result<Nothing>> {
        val itemForSynshronization = learningItem.toLearningItemAPI().copy(deletedStatus = true)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Complete)
    }

    private fun getActualItemForSynk(learningItem: LearningItem): LearningItemAPI {
        var itemForSynshronization = synkPreferences.getActualLearnItemsSynronization()
            .firstOrNull { it.timeStampUIID == learningItem.timeStampUIID }
        return itemForSynshronization ?: learningItem.toLearningItemAPI()
    }

    suspend fun changeItemsStatus(learningItem: LearningItem) = flow<Result<Long>> {
        var itemForSynshronization =
            getActualItemForSynk(learningItem).copy(learningStatus = learningItem.learningStatus)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        learningItemsRepository.writeToLocalDatabase(itemForSynshronization.toLearningItem())
            .collect {}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Success(learningItem.timeStampUIID))
    }


}