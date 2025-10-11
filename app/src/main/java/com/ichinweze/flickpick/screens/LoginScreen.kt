package com.ichinweze.flickpick.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN

@Composable
fun LoginScreen(navController: NavController) {

    val usernameInput = remember { mutableStateOf("") }
    val passwordInput = remember { mutableStateOf("") }

    val usernameHint = stringResource(R.string.login_username_hint)
    val passwordHint = stringResource(R.string.login_password_hint)
    val signInText = stringResource(R.string.login_sign_in)

    Surface {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.login_welcome),
                color = Color.Red,
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = usernameInput.value,
                onValueChange = { usernameInput.value = it },
                label = { Text(text = usernameHint, color = Color.Gray) },
                modifier = Modifier
                    .width(450.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(15.dp))

            TextField(
                value = passwordInput.value,
                onValueChange = { passwordInput.value = it },
                label = { Text(text = passwordHint, color = Color.Gray) },
                modifier = Modifier
                    .width(450.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(15.dp))

            Button(
                onClick = {
                    // TODO: Add user persistence to DB
                    // TODO: Add check for whether user is logged in
                    // TODO: Input checks
                    // TODO: Options for signing in/signing up/forgot password/remember me
                    navController.navigate(DASHBOARD_SCREEN)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Green
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = signInText,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}