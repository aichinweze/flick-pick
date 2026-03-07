package com.ichinweze.flickpick.data.firestore

data class UserAccountDetails(
    val name: String? = null,
    val age: String? = null
)

data class BaselineQuestion(
    val questionIndex: Int? = null,
    val question: String? = null,
    val responses: List<String>? = null
)

data class SelectedMovieDetails(
    val movieId: Int? = null,
    val movieTitle: String? = null,
    val posterPath: String? = null,
    val releaseDate: String? = null,
    val userRating: Float? = null
)