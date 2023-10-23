package com.learn.worlds.data.model.remote

data  class LearningItemAPI(val nativeData: String,
                            val foreignData: String,
                            val learningStatus: String = "LEARNING",
                            var timeStampUIID: Long)


