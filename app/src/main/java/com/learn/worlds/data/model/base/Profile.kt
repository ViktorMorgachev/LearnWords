package com.learn.worlds.data.model.base

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable


enum class AccountType{
    Base, Premium
}
@Immutable
@Serializable
data class Profile(val firstName: String, val secondName: String, val email: String, val accountType: AccountType = AccountType.Base, val balance: Balance)
