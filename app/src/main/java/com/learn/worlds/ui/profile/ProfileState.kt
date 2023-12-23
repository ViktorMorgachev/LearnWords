package com.learn.worlds.ui.profile

import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.ui.preferences.Preferences
import javax.annotation.concurrent.Immutable

sealed class ProfileEvent {
    data class onUpdateProfilePrefence(val preferences: Preferences.ProfilePreference): ProfileEvent()
}
@Immutable
data class ProfileState(
    val profilePrefs: List<Preferences.ProfilePreference> = listOf(),
    val profile: Profile? = null,
    val loadingState: Boolean = false
)