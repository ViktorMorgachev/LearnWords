package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.di.SynckPreferences
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SynckSharedPreferencesProfile @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {

    enum class SynkPreferenceKeys(val desc: String) {
        Profile("profileForSynchronization"),
        NeedUpdateRemoteProfile("profileNeedRemoteUpdate"),
        AnonimUserBalance("AnonimUserBalance"),
        AnonimUserBalanceWasInitialized("AnonimBalanceWasInitialized")
    }

    // Плохой подход немного но при работе с firebase базой напрямую приходится жертвовать приницами SOLID
    /* В данном случае мы должны напрямую как есть при начилии интернета записать в базу напрямую (не забываем что тут firebase)
    * Предложение учитываются) */


    // Profile

    var anonimUserBalance: Float = 0.0f
        get() {
            return  sharedPrefs.getFloat(SynkPreferenceKeys.AnonimUserBalance.desc, 0.0f)
        }
        set(value) {
            if (anonimBalanceWasInitialized) return
            sharedPrefs.edit().putFloat(SynkPreferenceKeys.AnonimUserBalance.desc, value).apply()
            field = value
            anonimBalanceWasInitialized = true
        }

     var anonimBalanceWasInitialized: Boolean = false
        get() {
            return  sharedPrefs.getBoolean(SynkPreferenceKeys.AnonimUserBalanceWasInitialized.desc, false)
        }
        set(value) {
            if (!value) return
            sharedPrefs.edit().putBoolean(SynkPreferenceKeys.AnonimUserBalanceWasInitialized.desc, value).apply()
            field = value
        }

    var profileUpdated: Boolean = false
        get() {
          return  sharedPrefs.getBoolean(SynkPreferenceKeys.NeedUpdateRemoteProfile.desc, true)
        }
        set(value) {
            sharedPrefs.edit().putBoolean(SynkPreferenceKeys.NeedUpdateRemoteProfile.desc, value).apply()
            field = value
        }

    fun getProfile(): Profile? {
        sharedPrefs.getString(SynkPreferenceKeys.Profile.desc, null)?.let {
            return Json.decodeFromString(it)
        }
        return null
    }

    fun saveProfile(profile: Profile) {
        sharedPrefs.edit().putString(SynkPreferenceKeys.Profile.desc, Json.encodeToString(profile)).apply()
    }

    fun removeProfile() {
        sharedPrefs.edit().remove(SynkPreferenceKeys.Profile.desc).apply()
        profileUpdated = false
    }
}



