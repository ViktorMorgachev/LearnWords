package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.AccountType
import com.learn.worlds.data.model.base.Balance
import com.learn.worlds.data.model.base.BalanceType
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.FirebaseKey
import com.learn.worlds.data.model.remote.FirebaseKey.*
import com.learn.worlds.data.model.remote.FirebasePreference
import com.learn.worlds.data.model.remote.FirebaseValue
import com.learn.worlds.data.model.remote.FirebaseValue.*
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.model.remote.ProfileAPI
import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenSpellCheckResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceData.*
import com.learn.worlds.ui.preferences.PreferenceValue


fun FirebaseKey.toPreferenceData(): PreferenceData {
    return when (this) {
        language_of_list -> DefaultLanguageOfList
        language_of_memorization -> DefaultLanguageOfMemorization
        timer_value_of_memorization -> DefaultTimerOfMemorization
        speech_sound_gender -> DefaultSpeechSoundGender
        profile_gender -> DefaultProfileGender
        add_card_native_language -> DefaultNativeLanguage
        add_card_learning_language -> DefaultLearningLanguage
    }
}

fun PreferenceData.toFirebaseKey(): FirebaseKey {
    return when (this) {
        DefaultLanguageOfList -> language_of_list
        DefaultLanguageOfMemorization -> language_of_memorization
        DefaultTimerOfMemorization -> timer_value_of_memorization
        DefaultSpeechSoundGender -> speech_sound_gender
        DefaultProfileGender -> profile_gender
        DefaultLearningLanguage -> add_card_learning_language
        DefaultNativeLanguage -> add_card_native_language
    }
}

fun FirebaseValue.toPreferenceValue(): PreferenceValue {
    return when (this) {
        Native -> PreferenceValue.Native
        Foreign -> PreferenceValue.Foreign
        Random -> PreferenceValue.Random
        GenderSpeechMale -> PreferenceValue.GenderSpeechMale
        GenderSpeechFemale -> PreferenceValue.GenderSpeechFemale
        GenderProfileFemale -> PreferenceValue.GenderProfileFemale
        GenderProfileMale -> PreferenceValue.GenderProfileMale
        GenderProfileOther -> PreferenceValue.GenderProfileOther
        GenderProfileHide -> PreferenceValue.GenderProfileHide
        Russian -> PreferenceValue.Russian
        English -> PreferenceValue.English
        French -> PreferenceValue.French
    }
}

fun PreferenceValue.toFirebaseValue(): FirebaseValue {
    return when (this) {
        PreferenceValue.Native ->  Native
        PreferenceValue.Foreign -> Foreign
        PreferenceValue.Random -> Random
        PreferenceValue.GenderSpeechMale -> GenderSpeechMale
        PreferenceValue.GenderSpeechFemale -> GenderSpeechFemale
        PreferenceValue.GenderProfileFemale -> GenderProfileFemale
        PreferenceValue.GenderProfileMale -> GenderProfileMale
        PreferenceValue.GenderProfileOther -> GenderProfileOther
        PreferenceValue.GenderProfileHide -> GenderProfileHide
        PreferenceValue.Russian -> Russian
        PreferenceValue.English -> English
        PreferenceValue.French -> French
    }
}


fun LocalPreference.toFirebasePreference(): FirebasePreference{
  return FirebasePreference(firebaseKey = this.prefsData?.toFirebaseKey(), firebaseValue = this.prefValue?.toFirebaseValue())
}

fun FirebasePreference.toLocalPreference(): LocalPreference{
    return LocalPreference(prefsData = this.firebaseKey?.toPreferenceData(), prefValue = this.firebaseValue?.toPreferenceValue())
}


