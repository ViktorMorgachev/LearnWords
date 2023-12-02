package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.data.model.base.FileNamesForFirebase
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.profilePrefs
import com.learn.worlds.di.SynckPreferences
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.ui.preferences.Preferences
import com.learn.worlds.ui.preferences.key
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

class SynckSharedPreferencesLearnCards @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {

    enum class SynkPreferenceKeys(val desc: String) {
        LearningItem(desc = "allLearningItemsForSynchronization"),
        Mp3Files(desc = "allMp3ItemsForSynchronization"),
        Images("allImagesForSynchronization"),
    }

    // Плохой подход немного но при работе с firebase базой напрямую приходится жертвовать приницами SOLID
    /* В данном случае мы должны напрямую как есть при начилии интернета записать в базу напрямую (не забываем что тут firebase)
    * Предложение учитываются) */

    fun addWordForSync(learningItemAPI: LearningItemAPI): Boolean {
        val actualListForSyncronization = getActualLearnItemsSynronization().toMutableList()
        val itemForRemoving = actualListForSyncronization.firstOrNull { it.timeStampUIID == learningItemAPI.timeStampUIID }
        if (itemForRemoving != null) {
            actualListForSyncronization.remove(itemForRemoving)
        }
        actualListForSyncronization.add(learningItemAPI)
        sharedPrefs.edit().putString(
            SynkPreferenceKeys.LearningItem.desc,
            Json.encodeToString(actualListForSyncronization)
        ).apply()
        return true
    }

    fun removeAllItemsIdsForSynshronization() {
        sharedPrefs.edit().remove(SynkPreferenceKeys.LearningItem.desc).apply()
    }

    fun getActualLearnItemsSynronization(): List<LearningItemAPI> {
        sharedPrefs.getString(SynkPreferenceKeys.LearningItem.desc, null)?.let {
            return Json.decodeFromString(it)
        }
        return listOf()
    }

    // Mp3Files
    fun addMp3FileNameForSync(name: String): Boolean {
        var allData = getActualMp3NamesItemsSynronization()
        if (allData.data.contains(name)) return true
        val itemForRemoving = allData.data.firstOrNull { it == name }
        if (itemForRemoving != null) {
           allData =  allData.copy(data = allData.data.toMutableList().apply {
                add(name)
            })
        }
        sharedPrefs.edit().putString(SynkPreferenceKeys.Mp3Files.desc, Json.encodeToString(allData)).apply()
        return true
    }

    fun addMp3FilesNameForSync(name: List<String>): Boolean {
        sharedPrefs.edit().remove(SynkPreferenceKeys.Mp3Files.desc).apply()
       val actualData =  getActualMp3NamesItemsSynronization().data.plus(name).toMutableSet()
        sharedPrefs.edit().putString(SynkPreferenceKeys.Mp3Files.desc, Json.encodeToString(FileNamesForFirebase(actualData.toList()))).apply()
        return true
    }


    fun getActualMp3NamesItemsSynronization(): FileNamesForFirebase {
        sharedPrefs.getString(SynkPreferenceKeys.Mp3Files.desc, null)?.let {
            return Json.decodeFromString(it)
        }
        return FileNamesForFirebase()
    }


    // AllImages

    fun addImageNameForSync(name: String): Boolean {
        var allData = getActualImageNamesItemsSynronization()
        if (allData.data.contains(name)) return true
        val itemForRemoving = allData.data.firstOrNull { it == name }
        if (itemForRemoving != null) {
            allData =  allData.copy(data = allData.data.toMutableList().apply {
                add(name)
            })
        }
        sharedPrefs.edit().putString(SynkPreferenceKeys.Images.desc, Json.encodeToString(allData)).apply()
        return true
    }

    fun addImageFilesNameForSync(name: List<String>): Boolean {
        sharedPrefs.edit().remove(SynkPreferenceKeys.Images.desc).apply()
        val actualData =  getActualImageNamesItemsSynronization().data.plus(name).toMutableSet()
        sharedPrefs.edit().putString(SynkPreferenceKeys.Images.desc, Json.encodeToString(FileNamesForFirebase(actualData.toList()))).apply()
        return true
    }


    fun getActualImageNamesItemsSynronization(): FileNamesForFirebase {
        sharedPrefs.getString(SynkPreferenceKeys.Images.desc, null)?.let {
            return Json.decodeFromString(it)
        }
        return FileNamesForFirebase()
    }

}



