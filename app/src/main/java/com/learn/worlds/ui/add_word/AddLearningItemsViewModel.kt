package com.learn.worlds.ui.add_word

import androidx.lifecycle.ViewModel
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


@HiltViewModel
class AddLearningItemsViewModel  @Inject constructor(
    private val learningItemsRepository: LearningItemsRepository
) : ViewModel() {
     suspend fun addLearningItem(learningItem: LearningItem) = learningItemsRepository.addLearningItem(learningItem)

}


