package com.learn.worlds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import com.learn.worlds.data.prefs.SynckSharedPreferencesProfile
import com.learn.worlds.servises.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
   private val authService: FirebaseAuthService,
   private val synckPrefsProfile: SynckSharedPreferencesProfile,
   private val synckPrefsPreference: SynckSharedPreferencesPreferences
) : ViewModel(){

    val authState = authService.authState
    
    fun logout(){
        viewModelScope.launch {
            authService.logout().collect{
                synckPrefsPreference.removeProfilePrefs()
                synckPrefsProfile.removeProfile()
            }
        }
    }

}