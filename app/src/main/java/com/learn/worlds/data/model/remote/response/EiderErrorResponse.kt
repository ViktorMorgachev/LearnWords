package com.learn.worlds.data.model.remote.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EiderErrorResponse(
    @SerialName("error")
    val error: Error?
) {
    @Serializable
    data class Error(
        @SerialName("message")
        val message: Message?,
        @SerialName("type")
        val type: String?
    ) {
        @Serializable
        data class Message(
            @SerialName("providers")
            val providers: List<String?>?,
            @SerialName("text")
            val text: List<String?>?
        )
    }
}