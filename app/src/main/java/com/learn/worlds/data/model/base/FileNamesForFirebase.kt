package com.learn.worlds.data.model.base

import kotlinx.serialization.Serializable

@Serializable
data class FileNamesForFirebase(val data: List<String> = listOf())
