package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import androidx.annotation.Keep
import com.learn.worlds.di.UIPreferences
import timber.log.Timber

import javax.inject.Inject
class UISharedPreferences @Inject constructor(@UIPreferences private val sharedPrefs: SharedPreferences) {

    var isShowedLoginInfo: Boolean
        get() {
            return sharedPrefs.getBoolean("isShowedLoginInfo", false)
        }
        set(value) {
            sharedPrefs.edit().putBoolean("isShowedLoginInfo", value).apply()
        }

}



