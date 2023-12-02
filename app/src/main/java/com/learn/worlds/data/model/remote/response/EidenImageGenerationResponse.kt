package com.learn.worlds.data.model.remote.response


import com.google.gson.annotations.SerializedName
import timber.log.Timber

class EidenImageGenerationResponse : ArrayList<EidenImageGenerationResponse.EidenImageGenerationResponseItem>(){
    data class EidenImageGenerationResponseItem(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item?>? = null,
        @SerializedName("provider")
        val provider: String,
        @SerializedName("status")
        val status: String
    ) {
        data class Item(
            @SerializedName("image")
            val image: String? = null,
            @SerializedName("image_resource_url")
            val imageResourceUrl: String? = null
        )
    }

    fun totalCost(): Double{
        Timber.d("totalCost: ${this.mapNotNull { it.cost }.sum()}")
        return  this.map { it.cost }.filterNotNull().sum()
    }

    fun actualImageUri(fallbackProvider: String, actualProvider: String) : String?{
        return  this.firstOrNull { it.provider == actualProvider && it.status == "success" }?.items?.firstOrNull { it?.imageResourceUrl != null}?.imageResourceUrl ?:
        this.firstOrNull { it.provider == fallbackProvider && it.status == "success" }?.items?.firstOrNull{ it?.imageResourceUrl != null }?.imageResourceUrl
    }


}