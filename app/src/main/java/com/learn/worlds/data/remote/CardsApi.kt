package com.learn.worlds.data.remote

import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.request.SpellingCheckRequest
import com.learn.worlds.data.model.remote.request.TextToSpeechRequest
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenSpellCheckResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CardsApi {
    @Headers("Content-Type: application/json")
    @POST("/v2/audio/text_to_speech")
   suspend fun getTextsSpeech(@Body request: TextToSpeechRequest): EidenTextToSpeechResponse

    @Headers("Content-Type: application/json")
    @POST("/v2/image/generation")
    suspend  fun getImage(@Body request: ImageGenerationRequest): EidenImageGenerationResponse

    @POST("/v2/text/spell_check")
    suspend  fun spellCheck(@Body request: SpellingCheckRequest): EidenSpellCheckResponse
}