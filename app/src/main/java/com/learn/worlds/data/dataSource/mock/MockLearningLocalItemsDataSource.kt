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
        return  mutableListOf(
            LearningItemDB("hello", "hola", "LEARNING", 1657977381211),
            LearningItemDB("goodbye", "adios", "LEARNING", 1657977381212),
            LearningItemDB("thank you", "gracias", "LEARNING", 1657977381213),
            LearningItemDB("you're welcome", "de nada", "LEARNING", 1657977381214),
            LearningItemDB("please", "por favor", "LEARNING", 1657977381215),
            LearningItemDB("no problem", "no hay problema", "LEARNING", 1657977381216),
            LearningItemDB("I'm sorry", "lo siento", "LEARNING", 1657977381217),
            LearningItemDB("yes", "sí", "LEARNING", 1657977381218),
            LearningItemDB("no", "no", "LEARNING", 1657977381219),
            LearningItemDB("I don't know", "no lo sé", "LEARNING", 1657977381220)
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