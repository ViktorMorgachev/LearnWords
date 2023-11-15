package com.learn.worlds.data.remote

import com.learn.worlds.data.model.remote.request.TextToSpeechRequest
import com.learn.worlds.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class LearnCardApi @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
) {

    suspend fun getTextsSpeech(textToSpeechRequestKotlin: TextToSpeechRequest) =
        flow<Result<Any>> {
            Timber.d("request: $textToSpeechRequestKotlin")
        }.flowOn(dispatcher)
}
