package com.learn.worlds.data.prefs

import android.content.SharedPreferences

import javax.inject.Inject

class MySharedPreferences @Inject constructor(private val sharedPrefs: SharedPreferences) {
    var currentLimit: Int
        get() {
           return sharedPrefs.getInt("currentLimit", 3)
        }
        set(value) {
            sharedPrefs.edit().putInt("currentLimit", value).apply()
        }

    var dataBaseLocked: Boolean
        get() {
            return sharedPrefs.getBoolean("dataBaseLocked", false)
        }
        set(value) {
            sharedPrefs.edit().putBoolean("dataBaseLocked", value).apply()
        }
}



