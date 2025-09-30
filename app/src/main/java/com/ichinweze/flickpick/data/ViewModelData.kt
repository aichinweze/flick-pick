package com.ichinweze.flickpick.data

object ViewModelData {

    data class BaselineQuestionData(
        val index: Int,
        val question: String,
        val isOptional: Boolean
    )

    data class GenreData(val index: Int, val genre: String)

    data class MovieRegionData(val index: Int, val industryName: String, val country: String)

    const val SCREEN_UNINITIALISED = "screen_uninitialised"
    const val SCREEN_INITIALISING = "screen_initialising"
    const val SCREEN_INITIALISED = "screen_initialised"
}