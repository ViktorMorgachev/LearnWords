package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.local.LearningLocalItemsDataSource
import com.learn.worlds.data.dataSource.mock.LearningMockItemsDataSource
import com.learn.worlds.data.dataSource.remote.LearningRemoteItemsDataSource
import com.learn.worlds.data.mappers.toImageGeneration
import com.learn.worlds.data.mappers.toLearningItem
import com.learn.worlds.data.mappers.toLearningItemAPI
import com.learn.worlds.data.mappers.toLearningItemDB
import com.learn.worlds.data.mappers.toTextToSpeech
import com.learn.worlds.data.model.base.ImageGeneration
import com.learn.worlds.data.model.base.LearningItem
import com.learn.worlds.data.model.base.TextToSpeech
import com.learn.worlds.data.model.db.LearningItemDB
import com.learn.worlds.data.model.remote.LearningItemAPI
import com.learn.worlds.data.model.remote.response.EidenImageGenerationResponse
import com.learn.worlds.data.model.remote.response.EidenTextToSpeechResponse
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LearningItemsRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val localDataSource: LearningLocalItemsDataSource,
    private val remoteDataSource: LearningRemoteItemsDataSource,
    private val mockItemsDataSource: LearningMockItemsDataSource
) {

    val data: Flow<List<LearningItem>> = localDataSource.learningItems.transform<List<LearningItemDB>, List<LearningItem>>{ emit(it.map { it.toLearningItem() }) }

    suspend fun getDataFromDatabase() = localDataSource.fetchDatabaseItems()
        .transform<List<LearningItemDB>, List<LearningItem>>() { emit(it.map { it.toLearningItem() })
    }.flowOn(dispatcher)

    suspend fun uploadTextSpeechToFirebase(textToSpeech: TextToSpeech) = remoteDataSource.uploadTextSpeechToFirebase(textToSpeech.file)

    suspend fun uploadImageToFirebase(imageGeneration: ImageGeneration) = remoteDataSource.uploadImageToFirebase(imageGeneration.file)

    suspend fun getTextsSpeechFromFirebase(textToSpeech: TextToSpeech) = remoteDataSource.downloadTextsSpeechFromFirebase(textToSpeech)

    suspend fun getImageFromFirebase(imageGeneration: ImageGeneration) = remoteDataSource.downloadImageFromFirebase(imageGeneration)
    suspend fun getTextsSpeechUrlFromApi(textToSpeech: TextToSpeech) = remoteDataSource.getTextsSpeechFromApi(textToSpeech).transform<Result<EidenTextToSpeechResponse>, Result<TextToSpeech>> {
        if (it is Result.Success){
            emit(Result.Success(it.data.toTextToSpeech(actualTextToSpeech = textToSpeech)))
        } else {
            emit(Result.Error())
        }
    }

    suspend fun getImageUrlFromApi(imageGeneration: ImageGeneration) = remoteDataSource.getImageFromApi(imageGeneration).transform<Result<EidenImageGenerationResponse>, Result<ImageGeneration>> {
        if (it is Result.Success){
            emit(Result.Success(it.data.toImageGeneration(imageGeneration)))
        } else {
            emit(Result.Error())
        }
    }

    suspend fun loadFileSpeechFromApi(textToSpeech: TextToSpeech) =  remoteDataSource.downloadTextSpeechFromApi(textToSpeech = textToSpeech)

    suspend fun loadImageFromApi(imageGeneration: ImageGeneration) =  remoteDataSource.downloadImageFromApi(imageGeneration = imageGeneration)

    suspend fun fetchDataFromNetwork(needIgnoreRemovingItems: Boolean = true) = remoteDataSource.fetchDataFromNetwork(needIgnoreRemovingItems).transform<Result<List<LearningItemAPI>>, Result<List<LearningItem>>> {
        if (it is Result.Error){
            emit(it)
        }
        if (it is Result.Success){
            emit(Result.Success(it.data.map { it.toLearningItem() }))
        }
    }
    suspend fun removeItemFromLocalDatabase(itemID: Long) = localDataSource.removeItemByIDs(learningItemID = itemID)
    suspend fun removeItemsFromLocalDatabase(itemIDs: List<Long>) = localDataSource.removeItemsByIDs(learningItemIDs = itemIDs)
    suspend fun writeToLocalDatabase(learningItem: LearningItem) = localDataSource.addLearningItem(learningItem.toLearningItemDB())
    suspend fun writeToRemoteDatabase(learningItem: LearningItem) = remoteDataSource.addLearningItem(learningItem.toLearningItemAPI())
    suspend fun writeListToLocalDatabase(learningItem: List<LearningItem>) = localDataSource.addLearningItems(learningItem.map { it.toLearningItemDB() })

    suspend fun writeListToRemoteDatabase(learningItems: List<LearningItem>) = flow<Result<Nothing>> {
        val resultList: MutableList<Result<Nothing>> = mutableListOf()
        learningItems.forEach{
            resultList.add(remoteDataSource.addLearningItem(it.toLearningItemAPI()))
        }
        if (resultList.all { it is Result.Complete }){
            emit(Result.Complete)
        } else {
            emit(Result.Error())
        }
    }.flowOn(dispatcher)


}