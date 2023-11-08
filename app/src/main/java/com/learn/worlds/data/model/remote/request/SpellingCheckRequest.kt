package com.learn.worlds.data.model.remote.request


import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class SpellingCheckRequest(
    @SerializedName("attributes_as_list")
    val attributesAsList: Boolean? = null,
    @SerializedName("language")
    val language: String = "en",
    @SerializedName("providers")
    val providers: String = Provider.COHERE.name.lowercase(),
    @SerializedName("fallback_providers")
    val fallbackProviders: String = FallbackProvider.OPENAI.name.lowercase(),
    @SerializedName("response_as_dict")
    val responseAsDict: Boolean? = null,
    @SerializedName("show_original_response")
    val showOriginalResponse: Boolean? = null,
    @SerializedName("text")
    val text: String? = null
){
    @Keep
    enum class Provider(){
        COHERE
    }

    @Keep
    enum class FallbackProvider(){
        OPENAI
    }
}