package com.learn.worlds.data.model.base

enum class LearningStatus{
    LEARNING, KNOWLEDGE
}

data class LearningItem(val nativeData: String, val foreignData: String, val learningStatus: String = LearningStatus.LEARNING.name, val uid: Int = 0)
