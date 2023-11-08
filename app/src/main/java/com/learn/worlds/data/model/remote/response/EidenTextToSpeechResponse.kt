package com.learn.worlds.data.model.remote.response


import com.google.gson.annotations.SerializedName

data class EidenTextToSpeechResponse(
    @SerializedName("amazon")
    val amazon: Amazon? = null,
    @SerializedName("elevenlabs")
    val elevenlabs: Elevenlabs? = null,
    @SerializedName("google")
    val google: Google? = null,
    @SerializedName("ibm")
    val ibm: Ibm? = null,
    @SerializedName("lovoai")
    val lovoai: Lovoai? = null,
    @SerializedName("microsoft")
    val microsoft: Microsoft? = null
) {
    data class Amazon(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    data class Elevenlabs(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    data class Google(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    data class Ibm(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    data class Lovoai(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    data class Microsoft(
        @SerializedName("audio")
        val audio: String? = null,
        @SerializedName("audio_resource_url")
        val audioUrl: String? = null,
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("voice_type")
        val voiceType: Int? = null
    )

    fun totalCost(): Double{
        return  (ibm?.cost ?: 0.0) + (google?.cost ?: 0.0)
    }

    fun actualTextSpeechUri() : String?{
        return ibm?.audioUrl ?: google?.audioUrl
    }

}