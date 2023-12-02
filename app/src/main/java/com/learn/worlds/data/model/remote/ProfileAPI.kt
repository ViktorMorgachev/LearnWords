package com.learn.worlds.data.model.remote

import com.learn.worlds.data.model.base.BalanceType
import kotlinx.serialization.Serializable

@Serializable
data class ProfileAPI(
    val firstName: String = "",
    val secondName: String = "",
    val email: String = "",
    val preferences: List<FirebasePreference> = listOf(),
    val accountType: String = "",
    val balanceValue: Double = 0.0,
    val balanceType: String = BalanceType.CatCoin.name){
}
