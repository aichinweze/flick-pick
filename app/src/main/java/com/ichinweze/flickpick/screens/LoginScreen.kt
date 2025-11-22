package com.ichinweze.flickpick.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ichinweze.flickpick.R
import com.ichinweze.flickpick.data.ViewModelData.CHECKS_PASSED
import com.ichinweze.flickpick.data.ViewModelData.CONFIRM_PASSWORD_FIELD
import com.ichinweze.flickpick.data.ViewModelData.PASSWORD_FIELD
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECKING_CREDENTIALS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECK_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_SUCCESS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER_CHECKING_EMAIL
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER_CHECK_EMAIL_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER_COMPLETE
import com.ichinweze.flickpick.data.ViewModelData.VALID_ERROR_EMAIL_EXISTS
import com.ichinweze.flickpick.data.ViewModelData.VALID_ERROR_NO_USER
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import com.ichinweze.flickpick.screens.utils.TextAndButtonRow
import com.ichinweze.flickpick.screens.utils.TextFieldWithUpdatingState
import com.ichinweze.flickpick.viewmodels.LoginViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    loginViewModel: LoginViewModel,
    context: Context
) {

    val password = loginViewModel.password.collectAsState()
    val confirmPassword = loginViewModel.confirmPassword.collectAsState()
    val name = loginViewModel.name.collectAsState()
    val email = loginViewModel.email.collectAsState()

    val screenState = loginViewModel.screenState.collectAsState()
    val showPasswordState = loginViewModel.showPasswordState.collectAsState()
    val showConfirmPasswordState = loginViewModel.showConfirmPasswordState.collectAsState()

    val passwordHint = stringResource(R.string.login_password_hint)
    val nameHint = stringResource(R.string.register_give_name)
    val emailHint = stringResource(R.string.register_give_email)
    val confirmPasswordHint = stringResource(R.string.register_confirm_password)

    val textFieldModifier = Modifier.width(450.dp)

    val emailExists = loginViewModel.emailCheckResponse.collectAsState()
    val userExists = loginViewModel.credentialCheckResponse.collectAsState()

    // TODO: Add check for whether user is logged in
    loginViewModel.initialiseScreen()

    Surface {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            if (screenState.value == SCREEN_INITIALISED) {
                Text(
                    text = stringResource(R.string.login_welcome),
                    color = Color.Red,
                    fontSize = 50.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextAndButtonRow(
                    text = stringResource(R.string.login_existing_user),
                    buttonText = stringResource(R.string.login_sign_in),
                    onClickParam = SCREEN_LOGIN,
                    onClickFn = loginViewModel::updateScreenState
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextAndButtonRow(
                    text = stringResource(R.string.login_new_user),
                    buttonText = stringResource(R.string.login_register),
                    onClickParam = SCREEN_REGISTER,
                    onClickFn = loginViewModel::updateScreenState
                )
            }

            // TODO: Options for forgot password/remember me
            if (screenState.value.contains(SCREEN_LOGIN)) {
                if (screenState.value == SCREEN_LOGIN_SUCCESS && userExists.value) {
                    loginViewModel.assignCredentialCheckResponse(false)
                    println("LoginScreen: User exists. Logging in...")
                    navController.navigate(DASHBOARD_SCREEN)
                }

                if (screenState.value == SCREEN_LOGIN_CHECK_DONE) {
                    Toast
                        .makeText(context, VALID_ERROR_NO_USER, Toast.LENGTH_SHORT)
                        .show()

                    loginViewModel.updateScreenState(SCREEN_LOGIN)
                }

                Text(
                    text = stringResource(R.string.login_sign_in),
                    color = Color.Red,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                TextFieldWithUpdatingState(
                    state = email,
                    onValueChangeFn = loginViewModel::assignEmail,
                    textFieldHint = emailHint,
                    modifier = textFieldModifier
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextFieldWithUpdatingState(
                    state = password,
                    onValueChangeFn = loginViewModel::assignPassword,
                    textFieldHint = passwordHint,
                    modifier = textFieldModifier,
                    visualTransformation = PasswordVisualTransformation(),
                    showIconState = showPasswordState,
                    toggleIconStateParam = PASSWORD_FIELD,
                    toggleIconStateFn = loginViewModel::toggleShowPasswordState
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            loginViewModel.updateScreenState(SCREEN_INITIALISED)
                            loginViewModel.clearInputFields()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(
                            text = stringResource(R.string.back),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            val checkResponse = loginViewModel.checkLoginFieldsAreValid()

                            if (checkResponse == CHECKS_PASSED) {
                                loginViewModel.checkLoginDetails()
                                loginViewModel.updateScreenState(SCREEN_LOGIN_CHECKING_CREDENTIALS)
                            } else {
                                Toast
                                    .makeText(context, checkResponse, Toast.LENGTH_SHORT)
                                    .show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(
                            text = stringResource(R.string.login_sign_in),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            if (screenState.value.contains(SCREEN_REGISTER)) {
                if (screenState.value == SCREEN_REGISTER_CHECK_EMAIL_DONE) {
                    loginViewModel.assignEmailCheckResponse(false)

                    if (emailExists.value) {
                        Toast
                            .makeText(context, VALID_ERROR_EMAIL_EXISTS, Toast.LENGTH_SHORT)
                            .show()

                        loginViewModel.updateScreenState(SCREEN_REGISTER)
                    } else {
                        println("LoginScreen: registration checks complete. Creating User...")
                        loginViewModel.createLoginDetails()
                    }
                }
                if (screenState.value == SCREEN_REGISTER_COMPLETE) {
                    println("LoginScreen: registration checks complete. User created. Login details: ${loginViewModel.getLoginDetails()}")
                    loginViewModel.clearInputFields()
                    loginViewModel.updateScreenState(SCREEN_LOGIN)

                    Toast
                        .makeText(context, stringResource(R.string.first_signin), Toast.LENGTH_LONG)
                        .show()
                }

                Text(
                    text = stringResource(R.string.login_register),
                    color = Color.Red,
                    fontSize = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextFieldWithUpdatingState(
                    state = name,
                    onValueChangeFn = loginViewModel::assignName,
                    textFieldHint = nameHint,
                    modifier = textFieldModifier
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextFieldWithUpdatingState(
                    state = email,
                    onValueChangeFn = loginViewModel::assignEmail,
                    textFieldHint = emailHint,
                    modifier = textFieldModifier
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextFieldWithUpdatingState(
                    state = password,
                    onValueChangeFn = loginViewModel::assignPassword,
                    textFieldHint = passwordHint,
                    modifier = textFieldModifier,
                    visualTransformation = PasswordVisualTransformation(),
                    showIconState = showPasswordState,
                    toggleIconStateParam = PASSWORD_FIELD,
                    toggleIconStateFn = loginViewModel::toggleShowPasswordState
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextFieldWithUpdatingState(
                    state = confirmPassword,
                    onValueChangeFn = loginViewModel::assignConfirmPassword,
                    textFieldHint = confirmPasswordHint,
                    modifier = textFieldModifier,
                    visualTransformation = PasswordVisualTransformation(),
                    showIconState = showConfirmPasswordState,
                    toggleIconStateParam = CONFIRM_PASSWORD_FIELD,
                    toggleIconStateFn = loginViewModel::toggleShowPasswordState
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = {
                            loginViewModel.updateScreenState(SCREEN_INITIALISED)
                            loginViewModel.clearInputFields()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(
                            text = stringResource(R.string.back),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = {
                            if (screenState.value == SCREEN_REGISTER) {
                                val checkResponse = loginViewModel.checkRegisterFieldsAreValid()

                                if (checkResponse == CHECKS_PASSED) {
                                    loginViewModel.updateScreenState(SCREEN_REGISTER_CHECKING_EMAIL)
                                    loginViewModel.checkIfUsernameExists()
                                } else {
                                    Toast
                                        .makeText(context, checkResponse, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text(
                            text = stringResource(R.string.login_register),
                            fontSize = 25.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                if (screenState.value == SCREEN_REGISTER_CHECKING_EMAIL) {
                    Spacer(modifier = Modifier.height(15.dp))

                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = colorResource(R.color.purple_500),
                        trackColor = colorResource(R.color.purple_200)
                    )
                }
            }
        }
    }
}