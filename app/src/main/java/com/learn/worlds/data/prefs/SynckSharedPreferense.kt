package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.di.SynckPreferences
import javax.inject.Inject
class SynckSharedPreferences @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {

    fun addWordForRemoving(itemId: String): Boolean {
        val actualListForRemoving = getActualLearnItemsForRemoving().toMutableList()
        if (actualListForRemoving.contains(itemId)){
            return false
        }
        actualListForRemoving.add(itemId)
        sharedPrefs.edit().putString("needForRemoveItems", actualListForRemoving.joinToString(separator = ",")).apply()
        return true
    }

    fun removeAllItemsIdsForRemoving(){
        sharedPrefs.edit().remove("needForRemoveItems").apply()
    }
    fun getActualLearnItemsForRemoving(): List<String>{
        sharedPrefs.getString("needForRemoveItems", null)?.let {
            return it.split(",")
        }
        return listOf()
    }

}



