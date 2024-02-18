package com.learn.worlds.data.model.base

import com.learn.worlds.data.model.remote.CommonLanguage


data class SpellTextCheck(val suggestion: String? = null, val requestText: String, val cost: Double? = null, val language: CommonLanguage)
