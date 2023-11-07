package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.di.SynckPreferences
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
class SynckSharedPreferences @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {


    // Плохой подход немного но при работе с firebase базой напрямую приходится жертвовать приницами SOLID
    /* В данном случае мы должны напрямую как есть при начилии интернета записать в базу напрямую (не забываем что тут firebase)
    * Предложение учитываются) */
    @OptIn(ExperimentalSerializationApi::class)
    fun addWordForSync(learningItemAPI: LearningItemAPI): Boolean {
        val actualListForSyncronization = getActualLearnItemsSynronization().toMutableList()
        val itemForRemoving = actualListForSyncronization.firstOrNull { it.timeStampUIID == learningItemAPI.timeStampUIID }
        if (itemForRemoving != null){
            actualListForSyncronization.remove(itemForRemoving)
        }
        actualListForSyncronization.add(learningItemAPI)
        sharedPrefs.edit().putString("allItemsForSynchronization", Json.encodeToString(actualListForSyncronization)).apply()
        return true
    }

    fun removeAllItemsIdsForSynshronization(){
        sharedPrefs.edit().remove("allItemsForSynchronization").apply()
    }
    @OptIn(ExperimentalSerializationApi::class)
    fun getActualLearnItemsSynronization(): List<LearningItemAPI>{
        sharedPrefs.getString("allItemsForSynchronization", null)?.let {
            return Json.decodeFromString(it)
        }
        return listOf()
    }

}



