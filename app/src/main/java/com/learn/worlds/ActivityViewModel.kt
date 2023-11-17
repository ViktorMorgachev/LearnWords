package com.learn.worlds

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.learn.worlds.servises.FirebaseAuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityViewModel @Inject constructor(
   private val authService: FirebaseAuthService
) : ViewModel(){

    val authState = authService.authState
    
    fun logout(){
        viewModelScope.launch {
            authService.logout().collect{}
        }
    }

}