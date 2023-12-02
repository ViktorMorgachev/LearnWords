package com.learn.worlds.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.R
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(
    private val synckPrefsPreferences: SynckSharedPreferencesPreferences,
) : ViewModel() {

    private val _uiState: MutableStateFlow<PreferencesState> = MutableStateFlow(PreferencesState(actualPreferences = listOf(
           Preferences.SelecteablePreference(
               variants = listOf(
                   PreferenceValue.Native,
                   PreferenceValue.Foreign,
               ),
               preferenceData = PreferenceData.DefaultLanguageOfList,
               selectedVariant = synckPrefsPreferences.getPreferenceSelectedVariant(PreferenceData.DefaultLanguageOfList.key) ?:  PreferenceValue.Native,
               groupName = R.string.list_of_words,
           ),
           Preferences.SelecteablePreference(
               variants = listOf(
                   PreferenceValue.Native,
                   PreferenceValue.Foreign,
                   PreferenceValue.Random
               ),
               groupName = R.string.prefs_group_learn_screen,
               selectedVariant = synckPrefsPreferences.getPreferenceSelectedVariant(PreferenceData.DefaultLanguageOfMemorization.key) ?:  PreferenceValue.Native,
               preferenceData = PreferenceData.DefaultLanguageOfMemorization

           ),
           Preferences.SliderPreference(
               groupName = R.string.prefs_group_learn_screen,
               preferenceData = PreferenceData.DefaultTimerOfMemorization,
               range = 60..120,
               actualValue = synckPrefsPreferences.getPreferenceActualValue(PreferenceData.DefaultTimerOfMemorization.key) ?: "60"
           ),
        Preferences.SelecteablePreference(
            variants = listOf(
                PreferenceValue.GenderSpeechFemale,
                PreferenceValue.GenderSpeechMale
            ),
            groupName = R.string.prefs_others,
            selectedVariant = synckPrefsPreferences.getPreferenceSelectedVariant(PreferenceData.DefaultSpeechSoundGender.key) ?:  PreferenceValue.GenderSpeechFemale,
            preferenceData = PreferenceData.DefaultSpeechSoundGender
        )
       ))
    )
    val uiState = _uiState.asStateFlow()


    fun handleEvent(event: PreferencesEvent) {
        viewModelScope.launch {
            if (event is PreferencesEvent.onUpdatePreferences) {
                synckPrefsPreferences.savePreference(event.preferences)
                val oldList: MutableList<Preferences> = mutableListOf<Preferences>().apply { addAll(_uiState.value.actualPreferences) }
                oldList.firstOrNull { it.key == event.preferences.key }?.let {
                    val actualIndex = oldList.indexOf(it)
                    oldList.removeAt(actualIndex)
                    oldList.add(actualIndex, event.preferences)
                    _uiState.emit(_uiState.value.copy(actualPreferences = oldList))
                }
            }
        }

    }



}