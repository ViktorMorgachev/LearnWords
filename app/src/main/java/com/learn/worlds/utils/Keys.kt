package com.learn.worlds.utils

import android.os.Build
import com.learn.worlds.BuildConfig

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    val devicePreudoID: String by lazy { getDevicePseudoID() }

    private fun getDevicePseudoID(): String {
        val pseudoID = "35"
        val boardLength = Build.BOARD.length % 10
        val brandLength = Build.BRAND.length % 10
        val deviceLength = Build.DEVICE.length % 10
        val displayLength = Build.DISPLAY.length % 10
        val hostLength = Build.HOST.length % 10
        val idLength = Build.ID.length % 10
        val manufacturerLength = Build.MANUFACTURER.length % 10
        val modelLength = Build.MODEL.length % 10
        val productLength = Build.PRODUCT.length % 10
        val tagsLength = Build.TAGS.length % 10
        val typeLength = Build.TYPE.length % 10
        val userLength = Build.USER.length % 10

        val sum = pseudoID + boardLength + brandLength + deviceLength + displayLength + hostLength + idLength + manufacturerLength + modelLength + productLength + tagsLength + typeLength + userLength
        return sum
    }

    val token: String
        get() {
            return if (BuildConfig.DEBUG) {
                //  eidenTokenDev()
                eidenTokenProd()
            } else {
                eidenTokenProd()
            }
        }

    external fun eidenTokenDev(): String

    external fun eidenTokenProd(): String

    external fun eidenFileUriTestProd(): String
}