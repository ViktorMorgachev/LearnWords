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
        sharedPrefs.edit().putString("needForRemoveItems", actualListForRemoving.joinToString(prefix = "[", separator = ":", postfix = "]")).apply()
        actualListForRemoving.add(itemId)
        return true
    }

    fun removeItemForRemoving(itemId: String): Boolean{
        val actualListForRemoving = getActualLearnItemsForRemoving().toMutableList()
       val result =  actualListForRemoving.remove(itemId)
        sharedPrefs.edit().putString("needForRemoveItems", actualListForRemoving.joinToString(prefix = "[", separator = ":", postfix = "]")).apply()
        return result
    }

    fun removeAllItemsIdsForRemoving(): Boolean{
        sharedPrefs.edit().remove("needForRemoveItems").apply()
        return true
    }
    fun getActualLearnItemsForRemoving(): List<String>{
        sharedPrefs.getString("needForRemoveItems", null)?.let {
            return it.split(":")
        }
        return listOf()
    }

}



