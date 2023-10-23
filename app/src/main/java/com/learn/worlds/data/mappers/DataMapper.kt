package com.learn.worlds.data.mappers

import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI

fun LearningItemDB.toLearningItem(): LearningItem{
    return LearningItem(nativeData, foreignData, learningStatus, timeStampUIID)
}


fun LearningItem.toLearningItemDB(): LearningItemDB{
    return LearningItemDB(nativeData, foreignData, learningStatus, timeStampUIID)
}

fun LearningItem.toLearningItemAPI(): LearningItemAPI{
    return LearningItemAPI(nativeData, foreignData, learningStatus, timeStampUIID)
}

fun LearningItemDB.toLearningItemAPI(): LearningItemAPI{
    return LearningItemAPI(nativeData, foreignData, learningStatus, timeStampUIID)
}

fun LearningItemAPI.toLearningItemDB(): LearningItemDB{
    return LearningItemDB(nativeData, foreignData, learningStatus, timeStampUIID)
}
