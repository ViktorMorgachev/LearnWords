package com.learn.worlds.data.prefs

import android.content.SharedPreferences
import com.learn.worlds.di.SynckPreferences
import com.learn.worlds.di.UIPreferences
import javax.inject.Inject
class SynckSharedPreferences @Inject constructor(@SynckPreferences private val sharedPrefs: SharedPreferences) {

    fun addWordForRemoving(wordId: String): Boolean {
        val actualListForRemoving = getActualWordsForRemoving().toMutableList()
        if (actualListForRemoving.contains(wordId)){
            return false
        }
        sharedPrefs.edit().putString("needForRemoveItems", actualListForRemoving.joinToString(prefix = "[", separator = ":", postfix = "]")).apply()
        actualListForRemoving.add(wordId)
        return true
    }

    fun getActualWordsForRemoving(): List<String>{
        sharedPrefs.getString("needForRemoveItems", null)?.let {
            return it.split(":")
        }
        return listOf()
    }

}



