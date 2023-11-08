package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse

fun LearningItemDB.toLearningItem(): LearningItem {
    return LearningItem(nativeData, foreignData, learningStatus, timeStampUIID)
}

fun LearningItem.toLearningItemDB(): LearningItemDB {
    return LearningItemDB(nativeData, foreignData, learningStatus, timeStampUIID)
}

fun LearningItem.toLearningItemAPI(): LearningItemAPI {
    return LearningItemAPI(
        nativeData = nativeData,
        foreignData = foreignData,
        learningStatus = learningStatus,
        timeStampUIID = timeStampUIID
    )
}

fun LearningItemAPI.toLearningItem(): LearningItem {
    return LearningItem(nativeData, foreignData, learningStatus, timeStampUIID)
}


fun EidenImageGenerationResponse.toImageGeneration(imageGeneration: ImageGeneration): ImageGeneration{
    return imageGeneration.copy(
        actualFileUrl = actualImageUri(
            fallbackProvider = ImageGenerationRequest.FallbackProvider.STABILITYAI.name.lowercase(),
            actualProvider = ImageGenerationRequest.Provider.REPLICATE.name.lowercase()), totalCost = totalCost())
}
fun EidenTextToSpeechResponse.toTextToSpeech(actualTextToSpeech: TextToSpeech): TextToSpeech{
    return actualTextToSpeech.copy(actualFileUrl = actualTextSpeechUri(), totalCost = totalCost())
}

