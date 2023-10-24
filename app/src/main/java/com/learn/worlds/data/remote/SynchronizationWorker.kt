package com.learn.worlds.data.remote

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.prefs.MySharedPreferences
import com.learn.worlds.data.repository.LearningItemsRepository
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.di.MainDispatcher
import com.learn.worlds.utils.Result
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import timber.log.Timber
import com.learn.worlds.utils.Result as CustomResult

@HiltWorker
class SynchronizationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    @IoDispatcher private val ioDispather: CoroutineDispatcher,
    @MainDispatcher private val mainDispather: CoroutineDispatcher,
    private val learningItemsRepository: LearningItemsRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {

        /**
         *  Думаю пока на этом достаточно,в идеале писать в файл что
         *  вытащили с интернета  чтобы в след при
         *  сбое не трогать базу firebase, её желательно как можно реже трогать
         * */
        learningItemsRepository.fetchDataFromNetwork()
            .flowOn(ioDispather)
            .filter { it != CustomResult.Loading && it != CustomResult.Complete}
            .transform<CustomResult<List<LearningItem>>, List<LearningItem>> { initialData ->
                if (initialData is CustomResult.Success) {
                    initialData.data
                }
                if (initialData is CustomResult.Error){
                    listOf<List<LearningItem>>()
                }
                else listOf()
            }
            .combine(learningItemsRepository.getDataFromDatabase().map { it.map { it.toLearningItem() } }) { fromNetwork, fromDatabase ->
            val dataForNetwork: MutableList<LearningItem> = mutableListOf()
            val dataForLocal: MutableList<LearningItem> = mutableListOf()
            fromDatabase.forEach { dbItem ->
                // Записываем в интернет если в ней нет элемента
                if (!fromNetwork.contains(dbItem)) {
                    dataForNetwork.add(dbItem)
                }
            }
            fromNetwork.forEach { remoteItem ->
                if (!fromDatabase.contains(remoteItem)) {
                    dataForNetwork.add(remoteItem)
                }
            }
                Timber.d("SynchronizationWorker: \n forRemote: ${dataForNetwork.joinToString(", ")} \n forLocal: ${dataForLocal.joinToString(", ")}")
        }.flowOn(mainDispather).collect()


        return Result.success()
    }
}