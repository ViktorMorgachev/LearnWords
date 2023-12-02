package com.learn.worlds.data.repository

import com.learn.worlds.data.dataSource.remote.AnonimBalanceDataSource
import com.learn.worlds.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class AnonimBalanceReposotory @Inject constructor(
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val remoteDataSource: AnonimBalanceDataSource
){
    suspend fun checkDeviceIDInDatabase(deviceId: String) = remoteDataSource.checkDeviceIDInDatabase(deviceId)
    suspend fun saveDeviceID(deviceId: String) = remoteDataSource.addDeviceID(deviceId)
}