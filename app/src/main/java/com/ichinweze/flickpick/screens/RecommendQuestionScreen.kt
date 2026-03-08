package com.ichinweze.flickpick.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOADED_RESULTS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOADING_RESULTS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_NO_RESULTS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REVIEW_SELECTION
import com.ichinweze.flickpick.data.ViewModelData.emptyProcessedResult
import com.ichinweze.flickpick.screens.utils.LabelAndContentTextRow
import com.ichinweze.flickpick.screens.utils.QuestionWithIndexAndContent
import com.ichinweze.flickpick.viewmodels.RecommendViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendQuestionScreen(
    navController: NavController,
    recommendViewModel: RecommendViewModel,
    context: Context
) {
    val screenState = recommendViewModel.screenState.collectAsState()
    val currQuestionIdx = recommendViewModel.currentQuestionIndex.collectAsState()
    val currQuestionStr = recommendViewModel.currentQuestion.collectAsState()
    val checklistOptions = recommendViewModel.checklistOptions.collectAsState()

    val numberOfQuestions = recommendViewModel.getNumberOfQuestions()

    val selectedIdx = recommendViewModel.selectedIndex.collectAsState()

    val searchResults = recommendViewModel.searchResults.collectAsState()
    val movieToReview = recommendViewModel.movieToReview.collectAsState()

    val noSelectionsToast = stringResource(R.string.toast_selected_0)

    LaunchedEffect(Unit) { recommendViewModel.initialiseScreen() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.flick_pick),
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red,
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = {
                        recommendViewModel.resetScreen()
                        navController.popBackStack()
                    }) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.arrow_back)
                        )
                    }
                }
            )
        },
        content = { paddingValues ->
            if (screenState.value == SCREEN_INITIALISED) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(start = 10.dp)
                    ) {
                        QuestionWithIndexAndContent(
                            questionIndex = currQuestionIdx.value,
                            questionContent = currQuestionStr.value
                        )
                    }

                    LazyColumn(
                        modifier = Modifier
                            .weight(8f)
                            .selectableGroup()
                    ) {
                        items(checklistOptions.value.size) { index ->
                            val itemObject = checklistOptions.value[index]

                            Row(
                                modifier = Modifier
                                    .padding(all = 10.dp)
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = selectedIdx.value == index,
                                        onClick = { recommendViewModel.updateSelectedIndex(index) },
                                        role = Role.RadioButton
                                    ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedIdx.value == index,
                                    onClick = null
                                )
                                Text(
                                    text = itemObject.checklistItem,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .weight(2f)
                            .padding(all = 10.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // Back button
                        if (currQuestionIdx.value > 0) {
                            Button(
                                onClick = { recommendViewModel.goBack() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                modifier = Modifier.padding(horizontal = 30.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.back),
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Next button
                        Button(
                            onClick = {
                                // Save data to view model
                                if (selectedIdx.value == -1) {
                                    Toast
                                        .makeText(context, noSelectionsToast, Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    recommendViewModel
                                        .updateResponseMap(currQuestionIdx.value, selectedIdx.value)

                                    if (currQuestionIdx.value < numberOfQuestions - 1) {
                                        recommendViewModel.goForward()
                                    }
                                    else {
                                        recommendViewModel.updateScreenState(SCREEN_LOADING_RESULTS)
                                        recommendViewModel.searchMovieApiForResults()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier.padding(horizontal = 30.dp)
                        ) {
                            val buttonText =
                                if (currQuestionIdx.value == numberOfQuestions - 1) {
                                    stringResource(R.string.done)
                                } else stringResource(R.string.next)

                            Text(
                                text = buttonText,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            if (screenState.value == SCREEN_LOADING_RESULTS) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(100.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Red,
                        trackColor = Color.Magenta,
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = stringResource(R.string.loading_search_results),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (screenState.value == SCREEN_LOADED_RESULTS) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(start = 2.dp, top = 10.dp, bottom = 15.dp, end = 7.dp)
                ) {
                    LazyColumn {
                        items(searchResults.value.size) { index ->
                            val searchResult = searchResults.value[index]

                            val imagePath = searchResult.posterPath
                            val imageUrl =
                                if (imagePath != null) {
                                    "https://image.tmdb.org/t/p/w300$imagePath"
                                } else ""

                            Row(
                                modifier = Modifier
                                    .height(125.dp)
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clickable {
                                        recommendViewModel.assignMovieToReview(searchResult)
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

                                Text(text = searchResult.title, fontSize = 35.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            if (screenState.value == SCREEN_REVIEW_SELECTION) {
                val resultOrNull = movieToReview.value.firstOrNull()
                val resultToReview =
                    if (resultOrNull != null) resultOrNull
                    else emptyProcessedResult

                val imagePath = resultToReview.posterPath
                val imageUrl =
                    if (imagePath != null) {
                        "https://image.tmdb.org/t/p/w300$imagePath"
                    } else ""

                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(10.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = resultToReview.title,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.5f)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (imageUrl != "") {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(R.string.search_result_description),
                            modifier = Modifier.weight(4f)
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.unavailable_image),
                            modifier = Modifier.weight(4f),
                            contentDescription = stringResource(R.string.search_result_description)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(
                        modifier = Modifier
                            .weight(1.5f),
                        horizontalAlignment = Alignment.Start
                    ) {
                        LabelAndContentTextRow(
                            stringResource(R.string.movie_release_date),
                            resultToReview.releaseDate,
                            Modifier.align(Alignment.Start)
                        )

                        LabelAndContentTextRow(
                            stringResource(R.string.movie_rating),
                            "${resultToReview.voteAverage} (${resultToReview.voteCount})",
                            Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = resultToReview.overview,
                        fontSize = 20.sp,
                        modifier = Modifier
                            .weight(2.8f)
                            .verticalScroll(rememberScrollState())
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .weight(1.5f)
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { recommendViewModel.unselectMovieForReview() },
                        ) {
                            Text(
                                text = stringResource(R.string.back),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                recommendViewModel.selectMovie()
                                navController.popBackStack()
                            },
                        ) {
                            Text(
                                text = stringResource(R.string.movie_selection),
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (screenState.value == SCREEN_NO_RESULTS) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.search_no_results),
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(25.dp))

                    Button(
                        onClick = {
                            recommendViewModel.resetScreen()
                            navController.popBackStack()
                        },
                    ) {
                        Text(
                            text = stringResource(R.string.back),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    )
}