package com.ichinweze.flickpick.screens.utils

import androidx.compose.ui.graphics.vector.ImageVector

object ScreenUtils {

    // Application screens
    const val LOGIN_SCREEN = "LOGIN_SCREEN"
    const val DASHBOARD_SCREEN = "DASHBOARD_SCREEN"
    const val BASELINE_Q_SCREEN = "BASELINE_Q_SCREEN"
    const val RECOMMEND_Q_SCREEN = "RECOMMEND_Q_SCREEN"
    const val ACCOUNT_INFO_SCREEN = "ACCOUNT_INFO_SCREEN"
    const val HISTORY_SCREEN = "HISTORY_SCREEN"

}

data class BottomNavigationItem(
    val title: String,
    val navigationScreen: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val hasNews: Boolean
)