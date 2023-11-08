package com.learn.worlds.data.model.remote.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class TextToSpeechRequest(
    @SerializedName("attributes_as_list")
    val attributesAsList: Boolean = false,
    @SerializedName("language")
    val language: String,
    @SerializedName("option")
    val option: String = "FEMALE",
    @SerializedName("providers")
    val providers: String = Provider.IBM.name.lowercase(),
    @SerializedName("audio_format")
    val audioFormat: String = "mp3",
    @SerializedName("response_as_dict")
    val responseAsDict: Boolean =  true,
    @SerializedName("show_original_response")
    val showOriginalResponse: Boolean = false,
    @SerializedName("text")
    val text: String,
    @SerializedName("volume")
    val volume: Int = 20,
    @SerializedName("fallback_providers")
    val fallbackProviders: String = FallbackProvider.GOOGLE.name.lowercase()
){

    @Keep
    enum class Provider(){
        IBM
    }

    @Keep
    enum class FallbackProvider(){
        GOOGLE
    }
}