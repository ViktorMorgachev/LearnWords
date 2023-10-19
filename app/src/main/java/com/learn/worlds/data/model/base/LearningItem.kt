package com.learn.worlds.data.model.base

enum class LearningStatus{
    LEARNING, LEARNED
}

data class LearningItem(val nativeData: String, val foreignData: String, val learningStatus: String = LearningStatus.LEARNING.name, val uid: Int = 0)

fun LearningItem.getActualText(showDefaultNative: Boolean): String{
   return if (showDefaultNative) nativeData else foreignData
}