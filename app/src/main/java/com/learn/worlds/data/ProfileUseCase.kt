package com.learn.worlds.data


import com.learn.worlds.data.model.base.AccountType
import com.learn.worlds.data.model.base.Balance
import com.learn.worlds.data.model.base.BalanceType
import com.learn.worlds.data.model.base.LocalPreference
import com.learn.worlds.data.model.base.Profile
import com.learn.worlds.data.prefs.SynckSharedPreferencesPreferences
import com.learn.worlds.data.prefs.SynckSharedPreferencesProfile
import com.learn.worlds.data.repository.ProfileRepository
import com.learn.worlds.defaultAnonimUserBalance
import com.learn.worlds.defaultNewUserBalance
import com.learn.worlds.di.IoDispatcher
import com.learn.worlds.servises.FirebaseAuthService
import com.learn.worlds.ui.preferences.PreferenceData
import com.learn.worlds.ui.preferences.PreferenceValue
import com.learn.worlds.utils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

val profilePrefs = listOf<PreferenceData>(PreferenceData.DefaultProfileGender)

class ProfileUseCase @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val firebaseAuthService: FirebaseAuthService,
    private val profileRepository: ProfileRepository,
    private val synkPrefsProfile: SynckSharedPreferencesProfile,
    private val synkPrefsPreference: SynckSharedPreferencesPreferences
) {

    private val scope = CoroutineScope(dispatcher)

    private fun actualProfilePreferences(): List<LocalPreference> {
        val allProfilePrefs: MutableList<LocalPreference> = mutableListOf()
        profilePrefs.forEach { prefData ->
            synkPrefsPreference.getPreferenceSelectedVariant(prefData.key)?.let {
                allProfilePrefs.add(LocalPreference(prefsData = prefData, prefValue = it))
            }
        }
        return allProfilePrefs
    }

    // Вызывается только при создании профиля а именно при регистрации новой почты,
    // но чтобы избежать финансовых потерь, учтём deviceID анонимного пользователя
    suspend fun addProfile(firstName: String, secondName: String) = flow<Result<Any>> {

        val actualEmail = firebaseAuthService.getUserEmail()
        if (actualEmail.isNullOrEmpty()) {
            emit(Result.Error())
            return@flow
        }

        val profile = Profile(
            firstName = firstName,
            secondName = secondName,
            email = actualEmail,
            accountType = AccountType.Base,
            balance = Balance(
                value = (defaultNewUserBalance + (synkPrefsProfile.anonimUserBalance).coerceAtMost((defaultNewUserBalance + defaultAnonimUserBalance).toFloat())),
                balanceType = BalanceType.CatCoin
            )
        )
        val profilePrefs = listOf(
            LocalPreference(
                prefsData = PreferenceData.DefaultProfileGender,
                prefValue = PreferenceValue.GenderProfileHide
            )
        )
        Timber.d("addProfile $profile, prefs: ${profilePrefs}")
        try {
            if (firebaseAuthService.isAuthentificated()) {
                val remoteAddingProfileResult = profileRepository.addRemoteProfile(
                    profile = profile,
                    profilePreferenceValues = profilePrefs
                )
                if (remoteAddingProfileResult is Result.Error) {
                    emit(Result.Error())
                } else {
                    synkPrefsProfile.saveProfile(profile = profile)
                    profilePrefs.forEach {
                        synkPrefsPreference.savePreference(it)
                    }
                    emit(Result.Complete)
                }
            }
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }

    suspend fun updateProfile(profile: Profile) = flow<Result<Nothing>> {
        Timber.d("updateProfile $profile")
        try {
            if (firebaseAuthService.isAuthentificated()) {
                synkPrefsProfile.profileUpdated = false
                synkPrefsProfile.saveProfile(profile)
                actualProfilePreferences().forEach {
                    synkPrefsPreference.savePreference(it)
                }
                val result = profileRepository.updateRemoteProfile(
                    profile = profile,
                    profilePreferenceValues = actualProfilePreferences()
                )
                if (result  is Result.Complete){
                    synkPrefsProfile.profileUpdated = true
                }
                emit(result)
            } else {
                emit(Result.Error())
            }
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }

    suspend fun initActualProfile() = flow<Result<Profile>> {
        try {
            var result: Result<Profile> = Result.Loading
            if (firebaseAuthService.isAuthentificated()) {
                profileRepository.fetchDataFromNetwork().collectLatest { pairResult ->
                    if (pairResult is Result.Error) {
                        val profile = synkPrefsProfile.getProfile()
                        val actualProfilePrefs = actualProfilePreferences()
                        if (profile != null && actualProfilePrefs.isNotEmpty()) {
                            result = Result.Success(profile)
                        } else {
                            synkPrefsPreference.savePreference(
                                LocalPreference(
                                    prefsData = PreferenceData.DefaultProfileGender,
                                    prefValue = PreferenceValue.GenderProfileHide
                                )
                            )
                            result = Result.Error()
                        }
                    }
                    if (pairResult is Result.Success) {
                        pairResult.data?.let { it ->
                            synkPrefsProfile.saveProfile(it.second)
                            it.first.forEach {
                                synkPrefsPreference.savePreference(it)
                            }
                            result = Result.Success(it.second)
                            return@collectLatest
                        }
                    } else{
                        result = Result.Error()
                    }
                }
            } else {
                result = Result.Error()
            }
            emit(result)
        } catch (t: Throwable) {
            Timber.e(t)
            emit(Result.Error())
        }
    }


}