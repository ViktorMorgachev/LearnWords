package com.learn.worlds.data.model.base

enum class SortingType{
    SORT_BY_NEW, SORT_BY_OLD
}
data class Sort(val sortingType: SortingType)
