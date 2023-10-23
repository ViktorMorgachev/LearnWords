package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import androidx.annotation.Keep

import javax.inject.Inject
class MySharedPreferences @Inject constructor(private val sharedPrefs: SharedPreferences) {


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
            return sharedPrefs.getBoolean("isAuthentificated", false)
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



