package com.ichinweze.flickpick.data

object ViewModelData {

    const val SCREEN_UNINITIALISED = "screen_uninitialised"
    const val SCREEN_INITIALISING = "screen_initialising"
    const val SCREEN_INITIALISED = "screen_initialised"

    data class QuestionData(
        val index: Int,
        val question: String,
        val isOptional: Boolean
    )

    data class GenreData(val index: Int, val genre: String)

    data class MovieRegionData(val index: Int, val industryName: String, val country: String)

    data class ReleaseDecadeData(
        val index: Int,
        val decadeLb: String,
        val decadeUb: String
    )

    data class MovieQualityData(
        val index: Int,
        val quality: String
    )

    data class MovieRuntimeData(
        val index: Int,
        val runtimeLb: String,
        val runtimeUb: String
    )
}