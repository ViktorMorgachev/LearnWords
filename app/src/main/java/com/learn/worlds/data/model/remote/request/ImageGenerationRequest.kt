package com.learn.worlds.data.model.remote.request

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

data class ImageGenerationRequest(
    @SerializedName("attributes_as_list")
    val attributesAsList: Boolean = false,
    @SerializedName("fallback_providers")
    val fallbackProviders: String = FallbackProvider.STABILITYAI.name.lowercase(),
    @SerializedName("num_images")
    val numImages: Int = 1,
    @SerializedName("providers")
    val providers: String = Provider.REPLICATE.name.lowercase(),
    @SerializedName("resolution")
    val resolution: String = "512x512",
    @SerializedName("response_as_dict")
    val responseAsDict: Boolean = false,
    @SerializedName("show_original_response")
    val showOriginalResponse: Boolean = false,
    @SerializedName("text")
    val text: String = ""
){

    @Keep
    enum class Provider{
       REPLICATE
    }

    @Keep
    enum class FallbackProvider{
        STABILITYAI
    }
}