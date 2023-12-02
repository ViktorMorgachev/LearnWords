package com.learn.worlds.data.model.base

import java.io.File


data class TextToSpeech(val speechFileName: String, val actualFileUrl: String? = null, val totalCost: Double? = null, val file: File, val genderType: GenderType){
    fun getFileNamePrefix(): String{
        return file.name.substringBefore(".").substringAfter("_")
    }
}
