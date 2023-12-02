package com.learn.worlds.data


import android.content.Context
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.model.base.GenderType
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.remote.CommonLanguage
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import com.learn.worlds.data.remote.ai.SpeechFileNameUtils
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.data.repository.LearningSynchronizationRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.utils.ErrorType
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.isImage
import com.learn.worlds.utils.isMp3File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class LearnItemsUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val firebaseAuthService: FirebaseAuthService,
    private val synkLearnCardPreferences: SynckSharedPreferencesLearnCards,
    private val synkPrefsPrefs: SynckSharedPreferencesPreferences,
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
            if (it is Result.Success) {
                if (it.data.suggestion?.lowercase() == spellTextCheck.requestText.lowercase()) {
                    spellCheckActual = spellCheckActual.copy(suggestion = "")
                } else {
                    spellCheckActual = spellCheckActual.copy(suggestion = it.data.suggestion)
                }

            }
        }
        if (spellCheckActual.suggestion == null) {
            emit(Result.Error(ErrorType.FAILED_TO_CHECK_SPELL_TEXT))
        } else {
            emit(Result.Success(spellCheckActual))
        }
    }

    suspend fun getImage(text: String) = flow<Result<ImageGeneration>> {
        val imageName = "${text}.jpg"
        var imageGeneration = ImageGeneration(file = File(context.cacheDir, imageName))
        val file = imageGeneration.file

        Timber.d("cardGeneration getImage: getLocalFile result: ${file.isImage()}")
        if (file.isImage()) {
            emit(Result.Success(imageGeneration))
        } else {
            learningItemsRepository.getImageFromFirebase(imageGeneration).collectLatest {}
            Timber.d("cardGeneration getImage: getImageFromFirebase  result: ${file.isImage()}")
            if (file.isImage()) {
                emit(Result.Success(imageGeneration))
            }
            learningItemsRepository.getImageUrlFromApi(imageGeneration).collectLatest {
                if (it is Result.Success) {
                    imageGeneration = it.data.copy(actualFileUrl = it.data.actualFileUrl)
                }
            }
            Timber.d("cardGeneration getImage: getImageUrlFromApi  result: ${imageGeneration.actualFileUrl}")
            if (imageGeneration.actualFileUrl != null) {
                learningItemsRepository.loadImageFromApi(imageGeneration).collectLatest {}
                Timber.d("cardGeneration getImage: loadImageFromApi  result: ${imageGeneration.file.isImage()}")
                if (!imageGeneration.file.isImage()) {
                    emit(Result.Complete)
                }
                learningItemsRepository.uploadImageToFirebase(imageGeneration).collectLatest {
                    Timber.d("cardGeneration getImage: uploadImageToFirebase  result: ${it::class.java}")
                    if (it is Result.Error) {
                        synkLearnCardPreferences.addImageNameForSync(imageName)
                    }
                }
                emit(Result.Success(imageGeneration))
            } else {
                emit(Result.Complete)
            }

        }
    }.flowOn(dispatcher)


    private fun getSpeechGenderVariant(): GenderType?{
        val defaultPreferenceVariant = synkPrefsPrefs.getPreferenceSelectedVariant(PreferenceData.DefaultSpeechSoundGender.key) ?: PreferenceValue.GenderSpeechFemale
        return if (defaultPreferenceVariant == PreferenceValue.GenderSpeechMale){
            GenderType.Male
        } else if (defaultPreferenceVariant == PreferenceValue.GenderSpeechFemale){
            GenderType.Female
        } else null
    }

    suspend fun getTextSpeech(text: String, language: CommonLanguage) = flow<Result<TextToSpeech>> {
        val speechGender = getSpeechGenderVariant()
        if (speechGender == null){
            emit(Result.Error())
        }
        val mp3FileName = SpeechFileNameUtils.getFileNameForFirebaseStorage(language = language, name = text, gender = speechGender!!.name.lowercase())
        var textToSpeech = TextToSpeech(speechFileName = mp3FileName, file = File(context.cacheDir, mp3FileName), genderType = speechGender)
        val file = textToSpeech.file
        Timber.d("cardGeneration getTextSpeech: getLocalFile result: ${file.isMp3File()}")
        if (file.exists() && file.isMp3File()) {
            emit(Result.Success(textToSpeech))
        } else {
            learningItemsRepository.getTextsSpeechFromFirebase(textToSpeech).collectLatest {}
            Timber.d("cardGeneration getTextSpeech: getTextsSpeechFromFirebase  result: ${file.isMp3File()}")
            if (file.isMp3File()) {
                emit(Result.Success(textToSpeech))
                return@flow
            }
            learningItemsRepository.getTextsSpeechUrlFromApi(textToSpeech).collectLatest {
                if (it is Result.Success) {
                    textToSpeech = it.data.copy(actualFileUrl = it.data.actualFileUrl)
                }
            }
            Timber.d("cardGeneration getTextSpeech: getTextsSpeechUrlFromApi  result: ${textToSpeech.actualFileUrl}")
            if (textToSpeech.actualFileUrl != null) {
                learningItemsRepository.loadFileSpeechFromApi(textToSpeech).collectLatest {}
                if (!textToSpeech.file.isMp3File()) {
                    emit(Result.Complete)
                }
                Timber.d("cardGeneration getTextSpeech: loadFileSpeechFromApi  result: ${textToSpeech.file.isMp3File()}")
                learningItemsRepository.uploadTextSpeechToFirebase(textToSpeech).collectLatest {
                    Timber.d("cardGeneration getTextSpeech: uploadTextSpeechToFirebase  result: ${it::class.java}")
                    if (it is Result.Error) {
                        synkLearnCardPreferences.addMp3FileNameForSync(mp3FileName)
                    }
                }
                emit(Result.Success(textToSpeech))
            } else {
                emit(Result.Complete)
            }

        }
    }.flowOn(dispatcher)

    suspend fun deleteWordItem(learningItem: LearningItem) = flow<Result<Nothing>> {
        val itemForSynshronization = learningItem.toLearningItemAPI().copy(deletedStatus = true)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkLearnCardPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Complete)
    }

    private fun getActualItemForSynk(learningItem: LearningItem): LearningItemAPI {
        var itemForSynshronization = synkLearnCardPreferences.getActualLearnItemsSynronization()
            .firstOrNull { it.timeStampUIID == learningItem.timeStampUIID }
        return itemForSynshronization ?: learningItem.toLearningItemAPI()
    }

    suspend fun changeItemsStatus(learningItem: LearningItem) = flow<Result<Long>> {
        var itemForSynshronization = getActualItemForSynk(learningItem).copy(learningStatus = learningItem.learningStatus)
        learningItemsRepository.removeItemFromLocalDatabase(learningItem.timeStampUIID).collect {}
        learningItemsRepository.writeToLocalDatabase(itemForSynshronization.toLearningItem())
            .collect {}
        val result = learningSynchronizationRepository.replaceRemoteItem(itemForSynshronization)
        if (result is Result.Error) {
            synkLearnCardPreferences.addWordForSync(itemForSynshronization)
        }
        emit(Result.Success(learningItem.timeStampUIID))
    }


}