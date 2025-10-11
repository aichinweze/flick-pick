package com.ichinweze.flickpick.data

import com.google.gson.annotations.SerializedName

object ApiData {

    data class MovieApiResponseData(
        val page: Int,
        val results: List<MoviePageResult>,
        @SerializedName("total_pages") val totalPages: Int,
        @SerializedName("total_results") val totalResults: Int
    )

    data class MoviePageResult(
        val adult: Boolean,
        @SerializedName("backdrop_path") val backdropPath: String?,
        @SerializedName("genre_ids") val genreIds: List<Int>,
        val id: Int,
        @SerializedName("original_language") val originalLanguage: String,
        @SerializedName("original_title") val originalTitle: String,
        val overview: String,
        val popularity: Float,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("release_date") val releaseDate: String,
        val title: String,
        val video: Boolean,
        @SerializedName("vote_average") val voteAverage: Float,
        @SerializedName("vote_count") val voteCount: Int
    )

    data class MovieGenreResponseData(val genres: List<MovieGenreResults>)

    data class MovieGenreResults(
        val id: Int,
        val name: String
    )
}