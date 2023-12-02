package com.learn.worlds.data.model.base

import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue


data class LocalPreference(val prefsData: PreferenceData?, val prefValue: PreferenceValue?){
    fun isCorrect(): Boolean {
        return prefValue != null && prefsData != null
    }
}
