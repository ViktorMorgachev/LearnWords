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

    }
}

fun PreferenceData.toFirebaseKey(): FirebaseKey {
    return when (this) {
        PreferenceData.DefaultLanguageOfList -> language_of_list
        PreferenceData.DefaultLanguageOfMemorization -> language_of_memorization
        PreferenceData.DefaultTimerOfMemorization -> timer_value_of_memorization
        PreferenceData.DefaultSpeechSoundGender -> speech_sound_gender
        PreferenceData.DefaultProfileGender -> profile_gender
    }
}

fun FirebaseValue.toPreferenceValue(): PreferenceValue {
    return when (this) {
        FirebaseValue.Native -> PreferenceValue.Native
        FirebaseValue.Foreign -> PreferenceValue.Foreign
        FirebaseValue.Random -> PreferenceValue.Random
        FirebaseValue.GenderSpeechMale -> PreferenceValue.GenderSpeechMale
        FirebaseValue.GenderSpeechFemale -> PreferenceValue.GenderSpeechFemale
        FirebaseValue.GenderProfileFemale -> PreferenceValue.GenderProfileFemale
        FirebaseValue.GenderProfileMale -> PreferenceValue.GenderProfileMale
        FirebaseValue.GenderProfileOther -> PreferenceValue.GenderProfileOther
        FirebaseValue.GenderProfileHide -> PreferenceValue.GenderProfileHide
    }
}

fun PreferenceValue.toFirebaseValue(): FirebaseValue {
    return when (this) {
        PreferenceValue.Native ->  FirebaseValue.Native
        PreferenceValue.Foreign   -> FirebaseValue.Foreign
        PreferenceValue.Random -> FirebaseValue.Random
        PreferenceValue.GenderSpeechMale -> FirebaseValue.GenderSpeechMale
        PreferenceValue.GenderSpeechFemale -> FirebaseValue.GenderSpeechFemale
        PreferenceValue.GenderProfileFemale -> FirebaseValue.GenderProfileFemale
        PreferenceValue.GenderProfileMale -> FirebaseValue.GenderProfileMale
        PreferenceValue.GenderProfileOther -> FirebaseValue.GenderProfileOther
        PreferenceValue.GenderProfileHide -> FirebaseValue.GenderProfileHide
    }
}


fun LocalPreference.toFirebasePreference(): FirebasePreference{
  return FirebasePreference(firebaseKey = this.prefsData?.toFirebaseKey(), firebaseValue = this.prefValue?.toFirebaseValue())
}

fun FirebasePreference.toLocalPreference(): LocalPreference{
    return LocalPreference(prefsData = this.firebaseKey?.toPreferenceData(), prefValue = this.firebaseValue?.toPreferenceValue())
}


