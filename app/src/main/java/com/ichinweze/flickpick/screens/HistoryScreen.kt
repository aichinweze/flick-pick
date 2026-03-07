package com.ichinweze.flickpick.screens

import android.app.AlertDialog
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ViewModelData.IMAGE_URL
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_EMPTY_HISTORY
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REVIEW_SELECTION
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REVIEW_SELECTION_EDIT
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.screens.utils.AccountNavigationItem
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.HistoryNavigationItem
import com.ichinweze.flickpick.screens.utils.HomeNavigationItem
import com.ichinweze.flickpick.screens.utils.LabelAndContentTextRow
import com.ichinweze.flickpick.viewmodels.HistoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    historyViewModel: HistoryViewModel,
    context: Context
) {
    LaunchedEffect(Unit) { historyViewModel.initialiseScreen() }

    val bottomNavItems = listOf(
        AccountNavigationItem(),
        HomeNavigationItem(),
        HistoryNavigationItem()
    )

    val selectedNavBarIdx = 2
    val screenState = historyViewModel.screenState.collectAsState()
    val previouslySelectedMovies = historyViewModel.previouslySelectedMovieDetails.collectAsState()
    val sliderPosition = historyViewModel.sliderPosition.collectAsState()

    val builder: AlertDialog.Builder = AlertDialog.Builder(context)

    builder
        .setMessage(R.string.sure_check)
        .setTitle(R.string.delete_from_history)
        .setPositiveButton(R.string.yes) { dialog, which ->
            historyViewModel.deleteMovieFromHistory()
        }
        .setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }
        .setCancelable(true)

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
                )
            )
        },
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedNavBarIdx == index,
                        onClick = {
                            // Pop up to home (dashboard) or go to history screen
                            if (selectedNavBarIdx != index) {
                                historyViewModel.resetScreen()

                                navController.navigate(item.navigationScreen) {
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

            if (screenState.value == SCREEN_UNINITIALISED) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(vertical = 40.dp, horizontal = 10.dp),
                    text = stringResource(R.string.loading_screen),
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center
                )
            } else if (screenState.value == SCREEN_EMPTY_HISTORY) {
                Text(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(vertical = 40.dp, horizontal = 10.dp),
                    text = stringResource(R.string.no_history_message),
                    fontSize = 45.sp,
                    textAlign = TextAlign.Center
                )
            } else if (screenState.value.contains(SCREEN_REVIEW_SELECTION)) {
                val movieUnderReview = historyViewModel.getMovieUnderReview()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // edit/save & back button
                    if (screenState.value == SCREEN_REVIEW_SELECTION_EDIT) {
                        Button(
                            onClick = { historyViewModel.updateMovieEntry() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier
                                .weight(1f)
                                .padding(5.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .weight(1f)
                                .padding(15.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    historyViewModel.updateScreenState(SCREEN_UNINITIALISED)
                                    historyViewModel.initialiseScreen()
                                },
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(vertical = 5.dp, horizontal = 7.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.back),
                                    modifier = Modifier.fillMaxHeight(),
                                    tint = Color.Black
                                )
                            }

                            IconButton(
                                onClick = {
                                    historyViewModel.setDetailsToTrack()
                                    historyViewModel.updateScreenState(SCREEN_REVIEW_SELECTION_EDIT)
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

                    Spacer(modifier = Modifier.height(7.dp))

                    // title
                    Text(
                        text = movieUnderReview.movieTitle ?: "",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // poster image
                    if (movieUnderReview.posterPath != "" && movieUnderReview.posterPath != null) {
                        val imagePath = movieUnderReview.posterPath
                        val imageUrl = "$IMAGE_URL$imagePath"

                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(R.string.search_result_description),
                            modifier = Modifier.weight(3f)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.unavailable_image),
                            modifier = Modifier.weight(3f),
                            contentDescription = stringResource(R.string.search_result_description)
                        )
                    }

                    Spacer(modifier = Modifier.height(7.dp))

                    // release date & user rating
                    val userRating =
                        if (movieUnderReview.userRating != null) movieUnderReview.userRating.toString()
                        else stringResource(R.string.please_provide_rating)

                    val userRatingFontWeight =
                        if (movieUnderReview.userRating != null) FontWeight.Normal
                        else FontWeight.Bold

                    Column(
                        modifier = Modifier
                            .weight(1.5f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        LabelAndContentTextRow(
                            stringResource(R.string.movie_release_date),
                            movieUnderReview.releaseDate ?: "",
                            Modifier.align(Alignment.Start)
                        )

                        Row(
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Text(
                                text = stringResource(R.string.user_rating),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(top = 5.dp, bottom = 5.dp)
                            )

                            Spacer(modifier = Modifier.padding(horizontal = 5.dp))

                            if (screenState.value == SCREEN_REVIEW_SELECTION_EDIT) {
                                Column(modifier = Modifier.fillMaxHeight()) {
                                    Slider(
                                        value = sliderPosition.value,
                                        onValueChange = { historyViewModel.setSliderPosition(it) },
                                        colors = SliderDefaults.colors(
                                            thumbColor = Color.Red,
                                            activeTrackColor = Color.Red,
                                            inactiveTrackColor = Color.Magenta
                                        ),
                                        steps = 100,
                                        valueRange = 0f..100f,
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Text(
                                        text = "${sliderPosition.value.toInt()}%",
                                        fontSize = 13.sp,
                                        textAlign = TextAlign.End
                                    )
                                }
                            } else {
                                Text(
                                    text = userRating,
                                    fontSize = 20.sp,
                                    fontWeight = userRatingFontWeight,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    // Delete entry button
                    Button(
                        onClick = {
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth()
                            .weight(1.75f)
                    ) {
                        Text(
                            text = stringResource(R.string.delete_from_history),
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.watch_history),
                        fontSize = 40.sp,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    LazyColumn {
                        items(previouslySelectedMovies.value.size) { movieIdx ->
                            val movieToDisplay = previouslySelectedMovies.value[movieIdx]

                            val imagePath = movieToDisplay.posterPath
                            val imageUrl = if (imagePath != null) "$IMAGE_URL$imagePath" else ""

                            Row(
                                modifier = Modifier
                                    .height(125.dp)
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        historyViewModel.reviewMovieRating(movieIdx)
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (imageUrl != "") {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = stringResource(R.string.search_result_description),
                                        modifier = Modifier.width(150.dp)
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(R.drawable.unavailable_image),
                                        modifier = Modifier.width(150.dp),
                                        contentDescription = stringResource(R.string.search_result_description)
                                    )
                                }

                                Spacer(modifier = Modifier.width(5.dp))

                                Column {
                                    Text(
                                        text = movieToDisplay.movieTitle ?: "",
                                        fontSize = 16.sp,
                                        modifier = Modifier.fillMaxWidth(),
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Row {
                                        Text(
                                            text = stringResource(R.string.movie_release_date),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = movieToDisplay.releaseDate ?: "",
                                            fontSize = 14.sp
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(5.dp))

                                    Row {
                                        Text(
                                            text = stringResource(R.string.user_rating),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        if (movieToDisplay.userRating == null) {
                                            Text(
                                                text = stringResource(R.string.please_provide_rating),
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Red
                                            )
                                        } else {
                                            Text(
                                                text = movieToDisplay.userRating.toString(),
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }
                }
            }
        }
    )
}