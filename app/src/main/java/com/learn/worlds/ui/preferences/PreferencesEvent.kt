package com.learn.worlds.ui.preferences

sealed class PreferencesEvent {
    data class onUpdatePreferences(val preferences: Preferences): PreferencesEvent()
}
data class PreferencesState(
    val actualPreferences : List<Preferences>
)