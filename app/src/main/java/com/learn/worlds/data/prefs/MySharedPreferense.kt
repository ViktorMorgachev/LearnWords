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



}



