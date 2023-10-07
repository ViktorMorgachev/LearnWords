package com.learn.worlds.data.model.db

enum class LearningStatusDB{
    LEARNING, KNOWLEDGE
}

data class LearningItemDB(val nativeData: String, val foreignData: String, val learningStatus: LearningStatusDB = LearningStatusDB.LEARNING, val wasTriedToShowDetailsByUser: Boolean = false)
