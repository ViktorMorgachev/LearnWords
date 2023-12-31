package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.db.LearningItemDB

fun LearningItemDB.toLearningItem(): LearningItem{
    return LearningItem(nativeData, foreignData, learningStatus, uid)
}


fun LearningItem.toLearningItemDB(): LearningItemDB{
    return LearningItemDB(nativeData, foreignData, learningStatus)
}
