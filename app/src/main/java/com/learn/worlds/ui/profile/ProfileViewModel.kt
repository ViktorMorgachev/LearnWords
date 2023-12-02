package com.learn.worlds.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.R
import com.learn.worlds.data.AnonimBalanceUseCase
import com.learn.worlds.data.ProfileUseCase
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import com.learn.worlds.data.prefs.SynckSharedPreferencesProfile
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.ui.preferences.Preferences
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val synckPrefsProfile: SynckSharedPreferencesProfile,
    private val synckPrefsPreferences: SynckSharedPreferencesPreferences,
    private val anonimBalanceUseCase: AnonimBalanceUseCase,
    private val profileUseCase: ProfileUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<ProfileState> = MutableStateFlow(ProfileState(profile = null, loadingState = true))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            anonimBalanceUseCase.checkDeviceIDStatusAndRegisterIfNeeds()
            var actualProfile : Profile? = null
            profileUseCase.initActualProfile().collectLatest { result ->
                Timber.d("initActualProfile: ${result}")
                if (result is Result.Success){
                    actualProfile = result.data
                }
            }
            emitActualProfile(profile = actualProfile, profilePreferences = getProfilePrefs())
        }
    }


    private fun getProfilePrefs(): List<Preferences.ProfilePreference>{
        return  listOf(
            Preferences.ProfilePreference(
                preferenceData = PreferenceData.DefaultProfileGender,
                selectedVariant = synckPrefsPreferences.getPreferenceSelectedVariant(PreferenceData.DefaultProfileGender.key) ?: PreferenceValue.GenderProfileHide,
                variants = listOf(
                    PreferenceValue.GenderProfileOther,
                    PreferenceValue.GenderProfileFemale,
                    PreferenceValue.GenderProfileMale,
                    PreferenceValue.GenderProfileHide
                ),
                icon = R.drawable.gender
            )
        )
    }
    private fun emitActualProfile(profile: Profile?, profilePreferences: List<Preferences.ProfilePreference>){
        Timber.d("emitActualProfile: profile: $profile profilePreferences: ${profilePreferences.map { it.selectedVariant }.joinToString(", ")}")
        viewModelScope.launch {
            with(_uiState.value){
                _uiState.emit(copy(profile = profile, profilePrefs = profilePreferences, loadingState = false))
            }
        }
    }

    private fun emitLoadingState(loadingState: Boolean){
        viewModelScope.launch {
            with(_uiState.value){
                _uiState.emit(copy(loadingState = loadingState))
            }
        }
    }

    fun handleEvent(event: ProfileEvent) {
        viewModelScope.launch {
            if (event is ProfileEvent.onUpdateProfilePrefence) {
                updateProfilePrefs(event.preferences)
            }
        }

    }

    private fun updateProfilePrefs(preferences: Preferences.ProfilePreference) {
        synckPrefsPreferences.savePreference(preferences)
        emitLoadingState(true)
        viewModelScope.launch {
            profileUseCase.updateProfile(profile = synckPrefsProfile.getProfile()!!).collectLatest {}
            emitActualProfile(profile = _uiState.value.profile, profilePreferences = getProfilePrefs())
            emitLoadingState(false)
        }


    }


}