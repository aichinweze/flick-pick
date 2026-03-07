package com.ichinweze.flickpick.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.screens.utils.AccountNavigationItem
import com.ichinweze.flickpick.screens.utils.BASELINE_Q_SCREEN
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.HistoryNavigationItem
import com.ichinweze.flickpick.screens.utils.HomeNavigationItem
import com.ichinweze.flickpick.screens.utils.RECOMMEND_Q_SCREEN

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(navController: NavController) {

    val bottomNavItems = listOf(
        AccountNavigationItem(),
        HomeNavigationItem(),
        HistoryNavigationItem()
    )

    val selectedNavBarIdx = 1

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.dashboard),
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavBarIdx == index,
                        onClick = {
                            if (selectedNavBarIdx != index) {
                                navController.navigate(item.navigationScreen)
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
                                        if (index == selectedNavBarIdx) {
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
                Button(
                    onClick = {
                        navController.navigate(BASELINE_Q_SCREEN)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_to_baseline),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    onClick = {
                        navController.navigate(RECOMMEND_Q_SCREEN)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .padding(horizontal = 25.dp)
                ) {
                    Text(
                        text = stringResource(R.string.dashboard_to_recommend),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    )
}