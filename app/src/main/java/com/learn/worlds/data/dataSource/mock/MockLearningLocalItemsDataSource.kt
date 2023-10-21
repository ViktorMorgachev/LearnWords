package com.learn.worlds.data.dataSource.mock


import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class MockLearningLocalItemsDataSource @Inject constructor(@IoDispatcher private val dispatcher: CoroutineDispatcher) {

    private val _learningItems: MutableStateFlow<MutableList<LearningItemDB>> = MutableStateFlow(initMockData())
    val learningItems: Flow<List<LearningItemDB>> = _learningItems.asStateFlow()

    private fun initMockData(): MutableList<LearningItemDB> {
        return mutableListOf(
            LearningItemDB("goodbye", "adios"),
            LearningItemDB("thank you", "gracias"),
            LearningItemDB("you're welcome", "de nada"),
            LearningItemDB("please", "por favor"),
            LearningItemDB("no problem", "no hay problema"),
            LearningItemDB("I'm sorry", "lo siento"),
            LearningItemDB("yes", "sí"),
            LearningItemDB("no", "no"),
            LearningItemDB("I don't know", "no lo sé")
        )
    }

    suspend fun addLearningItem(learningItemDB: LearningItemDB) = flow<Result<Any>> {
        Timber.e("addLearningItem: learningItemDB $learningItemDB")
        try {
            emit(Result.Loading)
            _learningItems.value.add(learningItemDB)
            emit(Result.Complete)
        } catch (t: Throwable){
            Timber.e(t)
            emit(Result.Error())
        }

    }


}