package com.learn.worlds.data.repository



import com.learn.worlds.data.dataSource.remote.ProfileRemoteItemsDataSource
import com.learn.worlds.data.mappers.toLocalPreference
import com.learn.worlds.data.mappers.toProfile
import com.learn.worlds.data.mappers.toProfileAPI
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.data.model.remote.ProfileAPI
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val remoteDataSource: ProfileRemoteItemsDataSource
) {
    suspend fun fetchDataFromNetwork() = remoteDataSource.fetchProfileData().transform<Result<ProfileAPI?>, Result<Pair<List<LocalPreference>, Profile>?>> {
        if (it is Result.Error){
            emit(it)
        }
        if (it is Result.Success){
            it.data?.let { profileResultApi->
                emit(Result.Success(profileResultApi.preferences.map { it.toLocalPreference() } to  profileResultApi.toProfile()))
                return@transform
            }
            emit(Result.Success(null))
        }
    }.flowOn(dispatcher)

    suspend fun addRemoteProfile(profile: Profile, profilePreferenceValues: List<LocalPreference>) = remoteDataSource.addProfileData(profile.toProfileAPI(profilePreferenceValues))

    suspend fun updateRemoteProfile(profile: Profile, profilePreferenceValues: List<LocalPreference>) = remoteDataSource.replaceProfileItem(profile.toProfileAPI(profilePreferenceValues))

}