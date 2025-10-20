package com.ichinweze.flickpick.data

import androidx.compose.ui.graphics.vector.ImageVector

object ScreenData {

    data class BottomNavigationItem(
        val title: String,
        val navigationScreen: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val hasNews: Boolean
    )

    data class AccountDetails(
        val name: String,
        val email: String,
        val age: Int?
    )

    data class BaselineDetails(
        val baselineQuestionIndex: Int,
        val baselineResponses: List<Int>
    )

    data class LoginDetails(
        val email: String,
        val password: String,
        val activeUser: Boolean
    )

    data class HistoricalDetails(
        val movieId: Int,
        val movieTitle: String,
        val movieRating: Float,
        val movieGenreIds: List<Int>
    )
}