package com.learn.worlds.ui.add_word

import androidx.lifecycle.ViewModel
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.repository.LearningItemsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel  @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository
) : ViewModel() {
     suspend fun addLearningItem(learningItem: LearningItem) =  learningItemsRepository.addLearningItem(learningItem)
}


