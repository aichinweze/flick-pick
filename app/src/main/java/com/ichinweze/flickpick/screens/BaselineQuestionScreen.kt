package com.ichinweze.flickpick.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.screens.utils.QuestionWithIndexAndContent
import com.ichinweze.flickpick.viewmodels.BaselineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaselineQuestionScreen(
    navController: NavController,
    baselineViewModel: BaselineViewModel,
    context: Context
) {

    val screenState = baselineViewModel.screenState.collectAsState()
    val currQuestionIdx = baselineViewModel.currentQuestionIndex.collectAsState()
    val currQuestionStr = baselineViewModel.currentQuestion.collectAsState()
    val checklistOptions = baselineViewModel.checklistOptions.collectAsState()

    val tooManySelectionsToast = stringResource(R.string.toast_already_3)
    val noSelectionsToast = stringResource(R.string.toast_selected_0)

    val numberOfQuestions = baselineViewModel.getNumberOfQuestions()

    LaunchedEffect(Unit) { baselineViewModel.initialiseScreen() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.baseline_q),
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
        content = { paddingValues ->
            if (screenState.value == SCREEN_INITIALISED) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1.5f)
                            .padding(start = 10.dp)
                            .fillMaxWidth()
                    ) {
                        QuestionWithIndexAndContent(
                            questionIndex = currQuestionIdx.value,
                            questionContent = currQuestionStr.value,
                            selectOption = stringResource(R.string.select_3)
                        )
                    }

                    LazyColumn(modifier = Modifier.weight(8f)) {
                        items(checklistOptions.value.size) { index ->
                            val itemObject = checklistOptions.value[index]

                            Row(
                                modifier = Modifier.padding(all = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = itemObject.isChecked,
                                    onCheckedChange = { it ->
                                        val checkedItems = checklistOptions.value.filter { item ->
                                            item.isChecked
                                        }

                                        if (checkedItems.size < 3 || itemObject.isChecked) {
                                            baselineViewModel.updateOptionStateAtIndex(index, it)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                tooManySelectionsToast,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    },
                                    modifier = Modifier.padding(horizontal = 10.dp)
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
                                onClick = {
                                    baselineViewModel.goBack()
                                },
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
                                val checkedItems = checklistOptions.value.filter { option ->
                                    option.isChecked
                                }

                                if (checkedItems.isEmpty()) {
                                    Toast.makeText(
                                        context,
                                        noSelectionsToast,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    baselineViewModel.updateResponseMap(currQuestionIdx.value, checkedItems)

                                    if (currQuestionIdx.value < numberOfQuestions - 1) {
                                        baselineViewModel.goForward()
                                    }
                                    else {
                                        baselineViewModel.finaliseQuestionResponses()
                                        baselineViewModel.persistQuestionResponses()

                                        baselineViewModel.resetScreen()
                                        navController.popBackStack()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                            modifier = Modifier.padding(horizontal = 30.dp)
                        ) {
                            val buttonText = if (currQuestionIdx.value == numberOfQuestions - 1) {
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
        }
    )
}