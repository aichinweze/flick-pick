package com.ichinweze.flickpick.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.BuildConfig
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECK_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_SUCCESS
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    context: Context
) {
    val screenState = loginViewModel.screenState.collectAsState()

    val webClientId = BuildConfig.WEB_CLIENT_ID

    Surface {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(R.string.login_welcome),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 50.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = stringResource(R.string.please_sign_in),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 35.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(30.dp))

            Image(
                painter = painterResource(R.drawable.siwg_button),
                contentDescription = "Sign in with Google",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(75.dp)
                    .padding(10.dp)
                    .clickable(
                        enabled = true,
                        onClick = {
                            loginViewModel.handleSignIn(webClientId, context)
                        }
                    )
            )
        }
    }

    LaunchedEffect(screenState.value) {
        if (screenState.value == SCREEN_LOGIN_CHECK_DONE) {
            Toast.makeText(context, "Sign In Success!", Toast.LENGTH_SHORT).show()
            loginViewModel.updateScreenState(SCREEN_LOGIN_SUCCESS)
            navController.navigate(DASHBOARD_SCREEN)
        }
    }
}