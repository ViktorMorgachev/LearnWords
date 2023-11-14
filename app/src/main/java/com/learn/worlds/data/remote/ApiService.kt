package com.learn.worlds.data.remote

import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.request.SpellingCheckRequest
import com.learn.worlds.data.model.remote.request.TextToSpeechRequest
import javax.inject.Inject

class ApiService @Inject constructor(
    private val cardsApi: CardsApi
) {
   suspend fun getTextsSpeech(textToSpeechResponseGson: TextToSpeechRequest? = null) = cardsApi.getTextsSpeech(textToSpeechResponseGson!!)

   suspend fun getImageGeneration(imageGenerationRequest: ImageGenerationRequest) = cardsApi.getImage(imageGenerationRequest)

    suspend fun spellCheck(spellingCheckRequest: SpellingCheckRequest) = cardsApi.spellCheck(spellingCheckRequest)

}