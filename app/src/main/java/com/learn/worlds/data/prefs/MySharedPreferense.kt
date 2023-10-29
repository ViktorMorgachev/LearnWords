package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.di.MainPreferences
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



