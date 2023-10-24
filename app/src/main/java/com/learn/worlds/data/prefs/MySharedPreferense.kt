package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import androidx.annotation.Keep
import com.learn.worlds.di.MainPreferences
import timber.log.Timber

import javax.inject.Inject
class MySharedPreferences @Inject constructor(@MainPreferences private val sharedPrefs: SharedPreferences) {

    var defaultLimit: Int = 3

    var dataBaseLocked: Boolean
        get() {
            return sharedPrefs.getBoolean("dataBaseLocked", false)
        }
        set(value) {
            sharedPrefs.edit().putBoolean("dataBaseLocked", value).apply()
        }

    var isAuthentificated: Boolean
        get() {
            val isAuthentificated = sharedPrefs.getBoolean("isAuthentificated", false)
            Timber.d("isAuthentificated: $isAuthentificated")
            return isAuthentificated
        }
        set(value) {
            sharedPrefs.edit().putBoolean("isAuthentificated", value).apply()
        }

    var subscribedByUser: Boolean
        get() {
            return sharedPrefs.getBoolean("subscribedByUser", false)
        }
        set(value) {
            sharedPrefs.edit().putBoolean("subscribedByUser", value).apply()
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



