package com.learn.worlds.data.model.remote.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class SpellingCheckRequest(
    @SerializedName("language")
    val language: String,
    @SerializedName("providers")
    val providers: String = Provider.NLPCLOUD.name.lowercase(),
    @SerializedName("fallback_providers")
    val fallbackProviders: String = FallbackProvider.OPENAI.name.lowercase(),
    @SerializedName("text")
    val text: String
){
    @Keep
    enum class Provider(){
      NLPCLOUD
    }

    @Keep
    enum class FallbackProvider(){
        OPENAI
    }
}