package com.learn.worlds.data.remote.ai

import com.learn.worlds.data.model.base.GenderType
import com.learn.worlds.data.model.remote.CommonLanguage
import com.learn.worlds.data.model.remote.FirebaseStorageLanguage
import com.learn.worlds.data.model.remote.TextToSpeechLanguage
import com.learn.worlds.servises.FirebaseStorageService
import java.io.File
import java.lang.UnsupportedOperationException
import javax.inject.Inject
import kotlin.jvm.Throws
object SpeechFileNameUtils {

    fun getFileNameForFirebaseStorage(language: CommonLanguage, name: String, gender: String) = "${gender}_${language.desc}_$name.mp3"

    @Throws(UnsupportedOperationException::class)
    fun getPathForFirebaseStorage(file: File) : FirebaseStorageService.Path{
        val gender = file.name.substringBefore("_")
        val language = file.name.substringAfter("_").substringBefore("_")
        val fileName = getFileNameForFirebaseStorage(file)
      return  when(language){
            CommonLanguage.English.desc -> FirebaseStorageService.Path.Speech(language = FirebaseStorageLanguage.English, name = fileName, gender = gender)
            CommonLanguage.French.desc -> FirebaseStorageService.Path.Speech(language = FirebaseStorageLanguage.French, name = fileName, gender = gender)
          else -> { throw UnsupportedOperationException("Speech filename isn't understand, actual name: ${fileName}," +
                  " only support format language_filename, and support only languages ${CommonLanguage.entries.map { it.desc }.joinToString(",")}")}
      }
    }

    fun getFileNameForFirebaseStorage(file: File): String{
        return file.name.substringAfterLast("_").substringBefore(".")
    }

    fun getLanguageForApi(file: File) : TextToSpeechLanguage {
        val fileName = file.name.substringAfter("_")
        val language = file.name.substringAfter("_").substringBefore("_")
        return  when(language){
            CommonLanguage.English.desc -> TextToSpeechLanguage.English
            CommonLanguage.French.desc -> TextToSpeechLanguage.French
            else -> { throw UnsupportedOperationException("Speech filename isn't understand, actual name: ${fileName}," +
                    " only support format language_filename, and support only languages ${CommonLanguage.entries.map { it.desc }.joinToString(",")}")}
        }
    }

}