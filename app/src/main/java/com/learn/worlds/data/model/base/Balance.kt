package com.learn.worlds.data.model.base

import androidx.annotation.DrawableRes
import androidx.annotation.IntegerRes
import androidx.compose.runtime.Immutable
import com.learn.worlds.R
import kotlinx.serialization.Serializable


enum class BalanceType(@DrawableRes val img: Int){
  CatCoin(img = R.drawable.cat_money)
}

@Immutable
@Serializable
data class Balance(val value: Double, val balanceType: BalanceType = BalanceType.CatCoin){

  fun getBalanceInDollars(): Double{
    return value / 100
  }
}
