package com.learn.worlds.data.model.remote.response


import com.google.gson.annotations.SerializedName
import timber.log.Timber

data class EidenSpellCheckResponse(
    @SerializedName("cohere")
    val cohere: Cohere? = null,
    @SerializedName("microsoft")
    val microsoft: Microsoft? = null,
    @SerializedName("nlpcloud")
    val nlpcloud: Nlpcloud? = null,
    @SerializedName("openai")
    val openai: Openai? = null,
    @SerializedName("prowritingaid")
    val prowritingaid: Prowritingaid? = null,
    @SerializedName("sapling")
    val sapling: Sapling? = null
) {
    data class Cohere(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: Any? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Int? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    data class Microsoft(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: String? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Int? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    data class Nlpcloud(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: Any? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Any? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    data class Openai(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: Any? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Int? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    data class Prowritingaid(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: String? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Any? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    data class Sapling(
        @SerializedName("cost")
        val cost: Double? = null,
        @SerializedName("items")
        val items: List<Item>? = null,
        @SerializedName("status")
        val status: String? = null,
        @SerializedName("text")
        val text: String? = null
    ) {
        data class Item(
            @SerializedName("length")
            val length: Int? = null,
            @SerializedName("offset")
            val offset: Int? = null,
            @SerializedName("suggestions")
            val suggestions: List<Suggestion>? = null,
            @SerializedName("text")
            val text: String? = null,
            @SerializedName("type")
            val type: Any? = null
        ) {
            data class Suggestion(
                @SerializedName("score")
                val score: Any? = null,
                @SerializedName("suggestion")
                val suggestion: String? = null
            )
        }
    }

    fun totalCost(): Double{
        Timber.d("totalCost: ${microsoft?.cost}:${openai?.cost}:${nlpcloud?.cost}")
        return  (microsoft?.cost ?: 0.0) + (openai?.cost ?: 0.0) + (nlpcloud?.cost ?: 0.0)
    }

    fun actualSuggestion() : String?{
        var suggestionResult: String? = null
        nlpcloud?.items?.map { it.suggestions }?.filterNotNull()?.forEach {
            suggestionResult = it.map { it.suggestion }.firstOrNull()
        }
        if (suggestionResult != null){
            return suggestionResult
        }
        val resultList: MutableList<String> = mutableListOf()
        openai?.items?.map { it.suggestions }?.filterNotNull()?.forEach { suggestion->
            suggestion.map { it.suggestion }.firstOrNull()?.let {
                resultList.add(it)
                resultList.add("\t")
            }
        }
        if (resultList.isNotEmpty()){
            suggestionResult = resultList.joinToString(separator = "")
        }
       return suggestionResult?.trimEnd()
    }



}