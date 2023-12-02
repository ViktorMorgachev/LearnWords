package com.learn.worlds.utils

import com.learn.worlds.data.AnonimBalanceUseCase
import com.learn.worlds.data.ProfileUseCase
import com.learn.worlds.defaultBalanceCooficient
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.FirebaseAuthService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.NumberFormat
import java.util.Currency
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BalanceProvider @Inject constructor(
    private val profileUseCase: ProfileUseCase,
    private val firebaseAuthService: FirebaseAuthService,
    private val anonimBalanceUseCase: AnonimBalanceUseCase,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {

    private val scope = CoroutineScope(GlobalScope.coroutineContext + dispatcher)

    companion object{
        val format: NumberFormat = NumberFormat.getCurrencyInstance().apply {
          maximumFractionDigits = 2
            currency = Currency.getInstance("USD")
      }
    }

    private var userBalance: Double = 0.0

    suspend fun getLastUserBalance(){
        profileUseCase.initActualProfile().collectLatest {
            if (it is Result.Success){
                userBalance = it.data.balance.value
            } else {
                userBalance = anonimBalanceUseCase.getAnonimBalance().toDouble()
            }
        }
    }

    // Итоговая функция которая получает итоговую стоимость в потраченных долларах,
    // и учитывая баланс пользователя, и коофициент баланса пользователя к долларам которые он может потратить, актуализирует баланс на сервере,
    // и обновляет профиль текущий

    fun spendedDollars(costInDollars: Double?){
        scope.launch {
            costInDollars?.let {
                getLastUserBalance()
                val spendedBalance = costInDollars / defaultBalanceCooficient
                Timber.d("spendedBalance: $costInDollars userBalance ${userBalance}")
                if (userBalance > spendedBalance){
                    updateUserActualBalance(userBalance - spendedBalance)
                } else {
                    updateUserActualBalance(newBalance = 0.0)
                }

            }
        }
    }

    private fun updateUserActualBalance(newBalance: Double) {
        scope.launch {
            var localProfile = firebaseAuthService.getLocalProfile()
            if (localProfile != null){
                val profileBalance = localProfile.balance
                localProfile =  localProfile.copy(balance = profileBalance.copy(value = newBalance))
                profileUseCase.updateProfile(localProfile).collectLatest {  }
            } else {
                anonimBalanceUseCase.setAnonimBalance(newBalance)
            }
        }


    }

}