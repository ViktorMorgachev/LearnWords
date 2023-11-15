package com.learn.worlds.data.model.base

import java.io.File

data class TextToSpeech(val actualFileUrl: String? = null, val totalCost: Double? = null, val file: File){
    fun getFileNamePrefix(): String{
        return file.name.substringBefore(".").substringAfter("_")
    }
}
