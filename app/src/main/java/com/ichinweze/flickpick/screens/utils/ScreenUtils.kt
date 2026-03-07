package com.ichinweze.flickpick.screens.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
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
        fontSize = 35.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.padding(all = 3.dp).fillMaxWidth()
    )

    Text(
        text = "${questionContent}$selectOptionForQuestion",
        fontSize = 15.sp,
        textAlign = TextAlign.Start,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LabelAndContentTextRow(
    label: String,
    content: String,
    rowModifier: Modifier
) {
    Row(modifier = rowModifier) {
        Text(
            text = label,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )

        Spacer(modifier = Modifier.padding(horizontal = 5.dp))

        Text(
            text = content,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
        )
    }
}

@Composable
fun TextFieldWithUpdatingState(
    state: State<String>,
    onValueChangeFn: (String) -> Unit,
    textFieldHint: String,
    modifier: Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    showIconState: State<Boolean>? = null,
    toggleIconStateParam: String = "",
    toggleIconStateFn: (String) -> Unit = { }
) {
    TextField(
        value = state.value,
        onValueChange = { onValueChangeFn(it) },
        label = { Text(text = textFieldHint, color = Color.Gray) },
        modifier = modifier,
        visualTransformation =
            if (showIconState != null && !showIconState.value) visualTransformation
            else VisualTransformation.None,
        trailingIcon = {
            if (showIconState != null) {
                val image =
                    if (showIconState.value) {
                        Icons.Filled.Visibility
                    } else Icons.Filled.VisibilityOff

                IconButton(onClick = { toggleIconStateFn(toggleIconStateParam) }) {
                    Icon(
                        imageVector = image,
                        contentDescription = stringResource(R.string.toggle_password)
                    )
                }
            }
        }
    )
}

@Composable
fun TextAndButtonRow(
    text: String,
    buttonText: String,
    onClickParam: String,
    onClickFn: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = text, fontSize = 30.sp)

        Spacer(modifier = Modifier.width(15.dp))

        Button(
            onClick = { onClickFn(onClickParam) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
        ) {
            Text(text = buttonText, fontSize = 30.sp)
        }
    }
}