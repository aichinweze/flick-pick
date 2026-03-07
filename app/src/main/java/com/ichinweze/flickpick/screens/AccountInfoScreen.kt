package com.ichinweze.flickpick.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ScreenData.BottomNavigationItem
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_EDIT_MODE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.screens.utils.ACCOUNT_INFO_SCREEN
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.HISTORY_SCREEN
import com.ichinweze.flickpick.viewmodels.AccountViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavController,
    accountViewModel: AccountViewModel,
    context: Context
) {
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

    val selectedState = accountViewModel.selectedNavBarState.collectAsState()

    accountViewModel.initialiseScreen()

    val accountName = accountViewModel.name.collectAsState()
    val accountEmail = accountViewModel.email.collectAsState()
    val accountAge = accountViewModel.age.collectAsState()

    val screenState = accountViewModel.screenState.collectAsState()

    val ageError = stringResource(R.string.age_error)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.account_info),
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
                        selected = selectedState.value == index,
                        onClick = {
                            // Pop up to home (dashboard) or go to history screen
                            if (selectedState.value != index) {
                                accountViewModel.updateSelectedNavBarState(index)
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
                                    if (index == selectedState.value) {
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
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .height(50.dp),
                    horizontalArrangement = Arrangement.Absolute.Right
                )  {
                    if (screenState.value == SCREEN_EDIT_MODE) {
                        Button(
                            onClick = {
                                if (accountAge.value.toString().matches("^\\d+$".toRegex())) {
                                    // Update data in Firestore
                                    accountViewModel.updateUserInformationInFirestore()

                                    // Update screen state to see new saved values
                                    accountViewModel.updateScreenState(SCREEN_INITIALISED)
                                } else {
                                    Toast.makeText(context, ageError,Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        IconButton(
                            onClick = {
                                accountViewModel.setPreviousUserDetailsToTrack()
                                accountViewModel.updateScreenState(SCREEN_EDIT_MODE)
                            },
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(vertical = 5.dp, horizontal = 7.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = stringResource(R.string.settings),
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Email
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp))  {
                    Text(
                        text = stringResource(R.string.email),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = accountEmail.value,
                        fontSize = 22.sp,
                        textAlign = TextAlign.Right
                    )
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Name
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
                    Text(
                        text = stringResource(R.string.name),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    if (screenState.value == SCREEN_EDIT_MODE) {
                        TextField(
                            value = accountName.value,
                            onValueChange = { accountViewModel.setAccountName(it) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        )
                    } else {
                        Text(
                            text = accountName.value,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Age
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp))  {
                    Text(
                        text = stringResource(R.string.age),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    if (screenState.value == SCREEN_EDIT_MODE) {
                        TextField(
                            value = accountAge.value.toString(),
                            onValueChange = { it -> accountViewModel.setAccountAge(it) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                        )
                    } else {
                        Text(
                            text = accountAge.value,
                            fontSize = 22.sp,
                            textAlign = TextAlign.Right
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Baseline Questions
                // TODO: Add Baseline questions to Account screen
                Row(modifier = Modifier.fillMaxWidth().padding(15.dp)) {
                    Text(
                        text = stringResource(R.string.baseline_q),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Left,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(15.dp))

                    Text(
                        text = "TBD",
                        fontSize = 22.sp,
                        textAlign = TextAlign.Right
                    )
                }
            }
        }
    )
}