package com.learn.worlds.data.model.base

enum class FilteringType{
    ALL, LEARNED
}
data class Filter(val filteringType: FilteringType)
