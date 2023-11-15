package com.learn.worlds.data.remote.ai

import com.learn.worlds.servises.FirebaseStorageService
import java.io.File

object ImageFileNameUtils {

    fun getPathForFirebaseStorage(file: File) : FirebaseStorageService.Path {
        val fileName = file.name.substringBefore(".")
        return FirebaseStorageService.Path.Image(fileName)
    }
}