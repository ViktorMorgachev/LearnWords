package com.learn.worlds.utils

import com.learn.worlds.BuildConfig

object Keys {

    init {
        System.loadLibrary("native-lib")
    }
    val token: String
        get() {
            return if (BuildConfig.DEBUG){
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