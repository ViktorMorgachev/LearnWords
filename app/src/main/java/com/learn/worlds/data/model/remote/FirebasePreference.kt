package com.learn.worlds.data.model.remote

import kotlinx.serialization.Serializable

enum class FirebaseKey{
    language_of_list, language_of_memorization,  timer_value_of_memorization,
    speech_sound_gender, profile_gender, add_card_native_language, add_card_learning_language
}

enum class FirebaseValue{
    Native, Foreign, Random, GenderSpeechMale, GenderSpeechFemale, GenderProfileFemale, GenderProfileMale, GenderProfileOther, GenderProfileHide, Russian, English, French,
}

@Serializable
data class FirebasePreference(val firebaseKey: FirebaseKey? = null, val firebaseValue: FirebaseValue? = null)

