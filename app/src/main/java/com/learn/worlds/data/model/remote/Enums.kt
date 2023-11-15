package com.learn.worlds.data.model.remote


enum class TextToSpeechLanguage(val desc: String){
    English("en"), French("fr")
}

enum class FirebaseStorageLanguage(val desc: String) {
    English("en"), French("fr")
}

enum class CommonLanguage(val desc: String) {
    English("en"), French("fr")
}


fun TextToSpeechLanguage.toFirebaseStorageLanguage(): FirebaseStorageLanguage{
   return when(this){
        TextToSpeechLanguage.English -> FirebaseStorageLanguage.English
        TextToSpeechLanguage.French ->  FirebaseStorageLanguage.French
    }
}

fun CommonLanguage.toFirebaseStorageLanguage(): FirebaseStorageLanguage{
    return when(this){
        CommonLanguage.English -> FirebaseStorageLanguage.English
        CommonLanguage.French ->  FirebaseStorageLanguage.French
    }
}

fun CommonLanguage.toTextToSpeechLanguage(): TextToSpeechLanguage{
    return when(this){
        CommonLanguage.English -> TextToSpeechLanguage.English
        CommonLanguage.French ->  TextToSpeechLanguage.French
    }
}