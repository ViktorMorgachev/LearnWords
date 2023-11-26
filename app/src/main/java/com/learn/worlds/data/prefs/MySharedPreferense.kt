package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.di.MainPreferences
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.ui.preferences.Preferences
import com.learn.worlds.ui.preferences.key
import timber.log.Timber
import javax.inject.Inject

class MySharedPreferences @Inject constructor(@MainPreferences private val sharedPrefs: SharedPreferences) {

    var savedSortingType: String?
        get() {
            return sharedPrefs.getString("savedSortingType", null)
        }
        set(value) {
            sharedPrefs.edit().putString("savedSortingType", value).apply()
        }

    var savedFilteringType: String?
        get() {
            return sharedPrefs.getString("savedFilteringType", null)
        }
        set(value) {
            sharedPrefs.edit().putString("savedFilteringType", value).apply()
        }

    fun savePreference(preferences: Preferences) {
        Timber.d("savePreference: $preferences")
        when (preferences) {
            is Preferences.CheckeablePreference -> {}
            is Preferences.SelecteablePreference -> {
                sharedPrefs.edit().putString(preferences.key, preferences.selectedVariant.key()).apply()
            }

            is Preferences.SliderPreference -> {
                sharedPrefs.edit().putString(preferences.key, preferences.actualValue.toString())
                    .apply()
            }
        }
    }

    fun getPreferenceActualVariant(key: String): PreferenceValue? {
        sharedPrefs.getString(key, null)?.let {
            return when (it) {
                PreferenceValue.Foreign.key() -> PreferenceValue.Foreign
                PreferenceValue.Native.key() -> PreferenceValue.Native
                PreferenceValue.Random.key() -> PreferenceValue.Random
                else -> null
            }
        }
        return null
    }

    fun getPreferenceActualValue(key: String): String? {
        return sharedPrefs.getString(key, null)
    }

}



