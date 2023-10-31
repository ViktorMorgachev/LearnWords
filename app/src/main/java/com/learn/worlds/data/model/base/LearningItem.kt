package com.learn.worlds.data.model.base

enum class LearningStatus{
    LEARNING, LEARNED
}

data class LearningItem(val nativeData: String, val foreignData: String, val learningStatus: String = LearningStatus.LEARNING.name, val timeStampUIID: Long = System.currentTimeMillis())

fun LearningItem.getActualText(showDefaultNative: Boolean, switched: Boolean): String{
   return if (!switched){
        if (showDefaultNative) nativeData else foreignData
    } else {
         if (showDefaultNative) foreignData else nativeData
    }
}