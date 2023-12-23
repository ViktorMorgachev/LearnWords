package com.learn.worlds.data.repository

import android.content.Context
import com.learn.worlds.data.dataSource.mock.LearningMockItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.model.base.FileNamesForFirebase
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.prefs.SynckSharedPreferencesLearnCards
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import com.learn.worlds.utils.isImage
import com.learn.worlds.utils.isMp3File
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningSynchronizationRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val synkPreferences: SynckSharedPreferencesLearnCards,
    private val remoteDataSource: LearningRemoteItemsDataSource,
    private val mockItemsDataSource: LearningMockItemsDataSource
) {

    suspend fun replaceRemoteItem(learningItem: LearningItemAPI) = remoteDataSource.replaceRemoteItems(learningItem)

    suspend fun fetchItemsIdsForRemoving() = remoteDataSource.fetchItemsIdsForRemoving()

    suspend fun uploadAllMp3Files(fileNamesForFirebase: FileNamesForFirebase) = flow<Result<Nothing>> {
        val filesForSaveLater = java.util.Collections.synchronizedList(mutableListOf<String>())
        val callbackFlows = fileNamesForFirebase.data
            .map { File(context.cacheDir, it) }
            .filter { it.isMp3File() }
            .map { file -> remoteDataSource.uploadTextSpeechToFirebase(file) }
        callbackFlows.forEachIndexed { index, flow ->
            flow.collectLatest {
                if (it is Result.Error){
                    filesForSaveLater.add(fileNamesForFirebase.data[index])
                }
            }
        }
        synkPreferences.addMp3FilesNameForSync(filesForSaveLater)
        emit(Result.Complete)
    }


    suspend fun uploadImageFiles(fileNamesForFirebase: FileNamesForFirebase) = flow<Result<Nothing>> {
        val filesForSaveLater = java.util.Collections.synchronizedList(mutableListOf<String>())
        val callbackFlows = fileNamesForFirebase.data
            .map { File(context.cacheDir, it) }
            .filter { it.isImage() }
            .map { file -> remoteDataSource.uploadImageToFirebase(file) }
        callbackFlows.forEachIndexed { index, flow ->
            flow.collectLatest {
                if (it is Result.Error){
                    filesForSaveLater.add(fileNamesForFirebase.data[index])
                }
            }
        }
        synkPreferences.addImageFilesNameForSync(filesForSaveLater)
        emit(Result.Complete)
    }

    suspend fun replaceRemoteItems(learningItems: List<LearningItemAPI>) = flow<Result<Nothing>> {
        val resultList: MutableList<Result<Nothing>> = mutableListOf()
        learningItems.forEach{
            resultList.add(remoteDataSource.replaceRemoteItems(it))
        }
        if (resultList.all { it is Result.Complete }){
            emit(Result.Complete)
        } else {
            emit(Result.Error())
        }
    }.flowOn(dispatcher)


}