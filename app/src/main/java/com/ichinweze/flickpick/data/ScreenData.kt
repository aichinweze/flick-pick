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
}