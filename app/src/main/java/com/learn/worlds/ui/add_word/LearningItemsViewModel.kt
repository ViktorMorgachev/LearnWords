package com.learn.worlds.ui.add_word

import androidx.lifecycle.ViewModel
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.repository.LearningItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LearningItemsViewModel  @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository
) : ViewModel() {

    fun saveLearningData(learningItem: LearningItem) {

    }
}