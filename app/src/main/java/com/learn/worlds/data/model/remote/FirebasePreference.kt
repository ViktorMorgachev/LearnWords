package com.learn.worlds.data.model.remote

import kotlinx.serialization.Serializable

enum class FirebaseKey{
    language_of_list, language_of_memorization,  timer_value_of_memorization,
    speech_sound_gender, profile_gender
}

enum class FirebaseValue{
    Native, Foreign, Random, GenderSpeechMale, GenderSpeechFemale, GenderProfileFemale, GenderProfileMale, GenderProfileOther, GenderProfileHide
}

@Serializable
data class FirebasePreference(val firebaseKey: FirebaseKey? = null, val firebaseValue: FirebaseValue? = null)

