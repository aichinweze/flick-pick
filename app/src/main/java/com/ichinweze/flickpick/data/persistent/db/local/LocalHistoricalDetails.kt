package com.ichinweze.flickpick.data.persistent.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ichinweze.flickpick.data.ScreenData.HistoricalDetails

@Entity(tableName = "historical-details")
data class LocalHistoricalDetails(
    @PrimaryKey val email: String,
    val movieId: Int,
    val movieTitle: String,
    val movieRating: Float,
    val movieGenreIds: String
)

fun LocalHistoricalDetails.toExternal() = HistoricalDetails(
    movieId = movieId,
    movieTitle = movieTitle,
    movieRating = movieRating,
    movieGenreIds = movieGenreIds.split("--").map { id -> id.toInt() }
)

fun HistoricalDetails.toLocal(email: String) = LocalHistoricalDetails(
    email = email,
    movieId = movieId,
    movieTitle = movieTitle,
    movieRating = movieRating,
    movieGenreIds = movieGenreIds.joinToString(separator = "--")
)