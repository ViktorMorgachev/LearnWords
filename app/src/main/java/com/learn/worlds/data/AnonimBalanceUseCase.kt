package com.learn.worlds.data


import com.learn.worlds.data.prefs.SynckSharedPreferencesProfile
import com.learn.worlds.data.repository.AnonimBalanceReposotory
import com.learn.worlds.defaultAnonimUserBalance
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Keys
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AnonimBalanceUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val balanceReposotory: AnonimBalanceReposotory,
    private val synckPrefsProfile: SynckSharedPreferencesProfile
) {

    private val scope = CoroutineScope(dispatcher)

    fun getAnonimBalance() = synckPrefsProfile.anonimUserBalance

    fun setAnonimBalance(balance: Double){
        synckPrefsProfile.anonimUserBalance = balance.toFloat()
    }
    fun checkDeviceIDStatusAndRegisterIfNeeds(){
        scope.launch {
            balanceReposotory.checkDeviceIDInDatabase(Keys.devicePreudoID).collectLatest {
                Timber.d("checkDeviceIDStatusAndRegisterIfNeeds: $this")
                if (it is Result.Success && !(it as Result.Success).data){
                    if (!synckPrefsProfile.anonimBalanceWasInitialized) {
                        synckPrefsProfile.anonimUserBalance = defaultAnonimUserBalance
                    }
                   balanceReposotory.saveDeviceID(Keys.devicePreudoID)
                }
            }

        }
    }

}