package com.learn.worlds.data.model.remote

import kotlinx.serialization.Serializable

@Serializable
data  class DeviceIdAPI(val device_id: String = "", val timestamp: Long = System.currentTimeMillis())


