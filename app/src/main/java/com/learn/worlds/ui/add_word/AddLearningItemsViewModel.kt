package com.learn.worlds.ui.add_word

import androidx.lifecycle.ViewModel
import com.learn.worlds.data.LearnItemsUseCase
import com.learn.worlds.data.model.base.LearningItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel  @Inject constructor(
    private val learnItemsUseCase: LearnItemsUseCase
) : ViewModel() {

     suspend fun addLearningItem(learningItem: LearningItem) = learnItemsUseCase.addLearningItem(learningItem)

}


