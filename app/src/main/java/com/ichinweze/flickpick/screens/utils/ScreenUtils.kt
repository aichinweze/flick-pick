package com.ichinweze.flickpick.screens.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ichinweze.flickpick.R


// Application screens
const val LOGIN_SCREEN = "LOGIN_SCREEN"
const val DASHBOARD_SCREEN = "DASHBOARD_SCREEN"
const val BASELINE_Q_SCREEN = "BASELINE_Q_SCREEN"
const val RECOMMEND_Q_SCREEN = "RECOMMEND_Q_SCREEN"
const val ACCOUNT_INFO_SCREEN = "ACCOUNT_INFO_SCREEN"
const val HISTORY_SCREEN = "HISTORY_SCREEN"


// Re-usable Composables
@Composable
fun QuestionWithIndexAndContent(
    questionIndex: Int,
    questionContent: String,
    selectOption: String = ""
) {
    val selectOptionForQuestion = if (selectOption != "") "\n$selectOption" else ""

    Text(
        text = "${stringResource(R.string.question)} ${questionIndex + 1}",
        fontSize = 40.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(all = 5.dp)
    )

    Text(
        text = "${questionContent}$selectOptionForQuestion",
        fontSize = 20.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier
            .padding(all = 5.dp)
    )
}