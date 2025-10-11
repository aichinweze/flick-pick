package com.ichinweze.flickpick.data

object ViewModelData {

    const val SCREEN_UNINITIALISED = "screen_uninitialised"
    const val SCREEN_INITIALISING = "screen_initialising"
    const val SCREEN_INITIALISED = "screen_initialised"
    const val SCREEN_LOADING_RESULTS = "screen_loading_results"
    const val SCREEN_LOADED_RESULTS = "screen_loaded_results"
    const val SCREEN_REVIEW_SELECTION = "screen_review_selection"
    const val SCREEN_NO_RESULTS = "screen_no_results"

    const val BASE_URL = "https://api.themoviedb.org/3/"

    const val AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MDBmZGZlNGY2OWY3MWY5YzFkNzJiNTAwZDBmMDE3OSIsIm5iZiI6MTc1OTU5OTE0OC4yNjIsInN1YiI6IjY4ZTE1YTJjYTIyNjYyN2FjZjI1YWIzZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ijkX6Eg1pTo-lZUChSjigXDTbl7ei33bUNtzqu6mFOA"

    data class QuestionData(
        val index: Int,
        val question: String,
        val isOptional: Boolean
    )

    data class GenreData(val index: Int, val genre: String)

    data class MovieRegionData(val index: Int, val industryName: String, val country: String)

    data class TimeBoundData(
        val index: Int,
        val timeBoundLb: String,
        val timeBoundUb: String
    )

    data class MovieQualityData(
        val index: Int,
        val quality: String
    )

    data class ProcessedResult(
        val id: Int,
        val genreIds: List<Int>,
        val title: String,
        val voteAverage: Float,
        val voteCount: Int,
        val popularity: Float,
        val posterPath: String?,
        val releaseDate: String,
        val overview: String
    )
}