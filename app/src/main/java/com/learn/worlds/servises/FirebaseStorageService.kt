package com.learn.worlds.servises


import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.learn.worlds.data.model.remote.FirebaseStorageLanguage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseStorageService @Inject constructor() {
    val storage by lazy { Firebase.storage }

    sealed class Path {
        data class Speech(val language: FirebaseStorageLanguage = FirebaseStorageLanguage.English, val name: String, val gender: String) : Path()
        data class Image(val name: String) : Path()
    }

    fun getStorageRef(path: Path): StorageReference {
      val storageRef = when(path){
            is Path.Image -> storage.reference.child("images/${path.name}.jpg")
            is Path.Speech ->  storage.reference.child("speech_list/${path.language.desc}/${path.gender}/${path.name}.mp3")
        }
        return storageRef
    }
}