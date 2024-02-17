package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.AccountType
import com.learn.worlds.data.model.base.Balance
import com.learn.worlds.data.model.base.BalanceType
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.data.model.base.SpellTextCheck
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.CommonLanguage
import com.learn.worlds.data.model.remote.FirebaseStorageLanguage
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.model.remote.ProfileAPI
import com.learn.worlds.data.model.remote.TextToSpeechLanguage
import com.learn.worlds.data.model.remote.request.ImageGenerationRequest
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenSpellCheckResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse
import com.learn.worlds.ui.preferences.PreferenceValue

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


fun EidenImageGenerationResponse.toImageGeneration(imageGeneration: ImageGeneration): ImageGeneration {
    return imageGeneration.copy(
        actualFileUrl = actualImageUri(
            fallbackProvider = ImageGenerationRequest.FallbackProvider.STABILITYAI.name.lowercase(),
            actualProvider = ImageGenerationRequest.Provider.REPLICATE.name.lowercase()
        ), totalCost = totalCost()
    )
}

fun EidenTextToSpeechResponse.toTextToSpeech(actualTextToSpeech: TextToSpeech): TextToSpeech {
    return actualTextToSpeech.copy(actualFileUrl = actualTextSpeechUri(), totalCost = totalCost())
}

fun EidenSpellCheckResponse.toSpellTextCheck(spellTextCheck: SpellTextCheck): SpellTextCheck {
    return spellTextCheck.copy(suggestion = actualSuggestion(), cost = totalCost())
}



fun Profile.toProfileAPI(profilePreferences: List<LocalPreference>): ProfileAPI {
    return ProfileAPI(
        firstName = firstName,
        secondName = secondName,
        email = email,
        preferences = profilePreferences.map { it.toFirebasePreference() },
        accountType = accountType.name,
        balanceValue = balance.value,
        balanceType = balance.balanceType.name
    )
}

fun ProfileAPI.toProfile(): Profile {
    return Profile(
        firstName = firstName, secondName = secondName, email = email,
        accountType = if (accountType == AccountType.Base.name) AccountType.Base else AccountType.Premium,
        balance = Balance(
            value = balanceValue,
            balanceType = if (balanceType == BalanceType.CatCoin.name) BalanceType.CatCoin else BalanceType.CatCoin
        )
    )
}

fun TextToSpeechLanguage.toFirebaseStorageLanguage(): FirebaseStorageLanguage {
   return when(this){
        TextToSpeechLanguage.English -> FirebaseStorageLanguage.English
        TextToSpeechLanguage.French -> FirebaseStorageLanguage.French
       TextToSpeechLanguage.Russian -> FirebaseStorageLanguage.Russian
   }
}

fun CommonLanguage.toFirebaseStorageLanguage(): FirebaseStorageLanguage {
    return when(this){
        CommonLanguage.English -> FirebaseStorageLanguage.English
        CommonLanguage.French -> FirebaseStorageLanguage.French
        CommonLanguage.Russian -> FirebaseStorageLanguage.Russian
    }
}

fun CommonLanguage.toTextToSpeechLanguage(): TextToSpeechLanguage {
    return when(this){
        CommonLanguage.English -> TextToSpeechLanguage.English
        CommonLanguage.French -> TextToSpeechLanguage.French
        CommonLanguage.Russian -> TextToSpeechLanguage.Russian
    }
}

fun PreferenceValue.toCommonLanguage(): CommonLanguage?{
    return  when(this){
        PreferenceValue.Russian -> CommonLanguage.Russian
        PreferenceValue.English -> CommonLanguage.English
        PreferenceValue.French -> CommonLanguage.French
        else -> null
    }
}