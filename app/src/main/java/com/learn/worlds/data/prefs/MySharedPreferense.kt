package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import androidx.annotation.Keep
import com.learn.worlds.di.MainPreferences
import timber.log.Timber

import javax.inject.Inject
class MySharedPreferences @Inject constructor(@MainPreferences private val sharedPrefs: SharedPreferences) {

    var isAuthentificated: Boolean
        get() {
            val isAuthentificated = sharedPrefs.getBoolean("isAuthentificated", false)
            Timber.d("get isAuthentificated: $isAuthentificated")
            return isAuthentificated
        }
        set(value) {
            Timber.d("set isAuthentificated: $value")
            sharedPrefs.edit().putBoolean("isAuthentificated", value).apply()
        }

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



