package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI

fun LearningItemDB.toLearningItem(): LearningItem{
    return LearningItem(nativeData, foreignData, learningStatus, uid)
}


fun LearningItem.toLearningItemDB(): LearningItemDB{
    return LearningItemDB(nativeData, foreignData, learningStatus)
}

fun LearningItemDB.toLearningItemAPI(): LearningItemAPI{
    return LearningItemAPI(nativeData, foreignData, learningStatus, uid)
}

fun LearningItemAPI.toLearningItemDB(): LearningItemDB{
    return LearningItemDB(nativeData, foreignData, learningStatus).apply {
        this@toLearningItemDB.uid = uid
    }
}
