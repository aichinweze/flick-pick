package com.ichinweze.flickpick.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ScreenData.BottomNavigationItem
import com.ichinweze.flickpick.screens.utils.ScreenUtils.ACCOUNT_INFO_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.ScreenUtils.HISTORY_SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    // TODO: Move State into a ViewModel
    // TODO: Move common items to a Utils

    val accountNavigationItem = BottomNavigationItem(
        title = stringResource(R.string.account_nav_item),
        navigationScreen = ACCOUNT_INFO_SCREEN,
        selectedIcon = Icons.Filled.Person,
        unselectedIcon = Icons.Outlined.Person,
        hasNews = false
    )

    // TODO: Can have news if user needs to provide feedback
    val historyNavigationItem = BottomNavigationItem(
        title = stringResource(R.string.history_nav_item),
        navigationScreen = HISTORY_SCREEN,
        selectedIcon = Icons.Filled.Info,
        unselectedIcon = Icons.Outlined.Info,
        hasNews = true
    )

    val homeNavigationItem = BottomNavigationItem(
        title = stringResource(R.string.home_nav_item),
        navigationScreen = DASHBOARD_SCREEN,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
        hasNews = false
    )

    val bottomNavItems = listOf(accountNavigationItem, homeNavigationItem, historyNavigationItem)

    val selectedState = remember { mutableIntStateOf(2) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.history),
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.arrow_back)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedState.intValue == index,
                        onClick = {
                            // Pop up to home (dashboard) or go to history screen
                            if (selectedState.intValue != index) {
                                selectedState.intValue = index
                                if (index == 1) navController.popBackStack()
                                else navController.navigate(item.navigationScreen) {
                                    popUpTo(DASHBOARD_SCREEN) { inclusive = false }
                                }
                            }
                        },
                        label = { Text(text = item.title) },
                        icon = {
                            BadgedBox(badge = {
                                // TODO: Check this is implemented
                                if (item.hasNews) Badge()
                            }) {
                                Icon(
                                    imageVector =
                                    if (index == selectedState.intValue) {
                                        item.selectedIcon
                                    } else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            }
                        }
                    )
                }
            }
        },
        content = { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "TBD: History For User",
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    )
}