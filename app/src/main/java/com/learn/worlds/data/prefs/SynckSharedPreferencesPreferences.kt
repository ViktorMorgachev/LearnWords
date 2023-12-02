package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.profilePrefs
import com.learn.worlds.di.SynckPreferences
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.ui.preferences.Preferences
import com.learn.worlds.ui.preferences.key
import kotlinx.serialization.ExperimentalSerializationApi
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class SynckSharedPreferencesPreferences @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {

    // Плохой подход немного но при работе с firebase базой напрямую приходится жертвовать приницами SOLID
    /* В данном случае мы должны напрямую как есть при начилии интернета записать в базу напрямую (не забываем что тут firebase)
    * Предложение учитываются) */

    fun savePreference(preferences: Preferences) {
        Timber.d("savePreference: $preferences")
        when (preferences) {
            is Preferences.CheckeablePreference -> {}
            is Preferences.SelecteablePreference -> {
                sharedPrefs.edit().putString(preferences.key, preferences.selectedVariant.key()).apply()
            }

            is Preferences.SliderPreference -> {
                sharedPrefs.edit().putString(preferences.key, preferences.actualValue.toString()).apply()
            }

            is Preferences.ProfilePreference -> {
                sharedPrefs.edit().putString(preferences.key, preferences.selectedVariant.key()).apply()
            }
        }
    }

    fun savePreference(localPreference: LocalPreference){
        if (localPreference.isCorrect()){
            sharedPrefs.edit().putString(localPreference.prefsData!!.key, localPreference.prefValue!!.key()).apply()
        }

    }

    fun getPreferenceSelectedVariant(key: String): PreferenceValue? {
        sharedPrefs.getString(key, null)?.let {
            return when (it) {
                PreferenceValue.Foreign.key() -> PreferenceValue.Foreign
                PreferenceValue.Native.key() -> PreferenceValue.Native
                PreferenceValue.Random.key() -> PreferenceValue.Random
                PreferenceValue.GenderSpeechFemale.key() -> PreferenceValue.GenderSpeechFemale
                PreferenceValue.GenderSpeechMale.key() -> PreferenceValue.GenderSpeechMale
                PreferenceValue.GenderProfileMale.key() -> PreferenceValue.GenderProfileMale
                PreferenceValue.GenderProfileFemale.key() -> PreferenceValue.GenderProfileFemale
                PreferenceValue.GenderProfileHide.key() -> PreferenceValue.GenderProfileHide
                PreferenceValue.GenderProfileOther.key() -> PreferenceValue.GenderProfileOther
                else -> null
            }
        }
        return null
    }

    fun removeProfilePrefs(prefs: List<PreferenceData> = profilePrefs){
        prefs.forEach {
            removePref(it.key)
        }
    }

    private fun removePref(key: String){
        sharedPrefs.edit().remove(key).apply()
    }



    fun getPreferenceActualValue(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

}



