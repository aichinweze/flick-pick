package com.ichinweze.flickpick.viewmodels

import android.content.Context
import androidx.credentials.CredentialManager
import android.util.Base64
import android.util.Log
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.ichinweze.flickpick.data.ScreenData.LoginDetails
import com.ichinweze.flickpick.data.ViewModelData.CHECKS_PASSED
import com.ichinweze.flickpick.data.ViewModelData.PASSWORD_FIELD
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECKING_CREDENTIALS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECK_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_SUCCESS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER_CHECK_EMAIL_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REGISTER_COMPLETE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.data.ViewModelData.VALID_ERROR_EMPTY_FIELDS
import com.ichinweze.flickpick.data.ViewModelData.VALID_ERROR_INVALID_EMAIL
import com.ichinweze.flickpick.data.ViewModelData.VALID_ERROR_PASSWORD_MISMATCH
import com.ichinweze.flickpick.repositiories.LoginRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom

class LoginViewModel(val loginRepository: LoginRepository): ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _showPasswordState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showPasswordState = _showPasswordState.asStateFlow()

    private val _confirmPassword: MutableStateFlow<String> = MutableStateFlow("")
    val confirmPassword = _confirmPassword.asStateFlow()

    private val _showConfirmPasswordState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val showConfirmPasswordState = _showConfirmPasswordState.asStateFlow()

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _emailCheckResponse: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val emailCheckResponse = _emailCheckResponse.asStateFlow()

    private val _credentialCheckResponse: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val credentialCheckResponse = _credentialCheckResponse.asStateFlow()

    private val TAG: String = "LoginViewModel"

    private var loginDetails: LoginDetails? = null

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            println("LoginViewModel: initialiseScreen: initialising screen...")
            updateScreenState(SCREEN_INITIALISED)
            /*viewModelScope.launch(Dispatchers.IO) {
                val activeUserExists = loginRepository.findActiveUser()

                updateScreenState(SCREEN_INITIALISED)

                // TODO: Move to Main screen?
                if (activeUserExists) {
                    // proceed to dashboard with active user
                } else {
                    // proceed to sign up or register
                }
            }*/
        }
    }

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    fun assignPassword(input: String) {
        _password.update { state -> input }
    }

    fun assignName(input: String) {
        _name.update { state -> input }
    }

    fun assignEmail(input: String) {
        _email.update { state -> input }
    }

    fun assignConfirmPassword(input: String) {
        _confirmPassword.update { state -> input }
    }

    fun assignEmailCheckResponse(bool: Boolean) {
        _emailCheckResponse.update { state -> bool }
    }

    fun assignCredentialCheckResponse(bool: Boolean) {
        _credentialCheckResponse.update { state -> bool }
    }

    fun toggleShowPasswordState(passwordType: String) {
        when(passwordType) {
            PASSWORD_FIELD      -> _showPasswordState.update { it -> !it }
            else                -> _showConfirmPasswordState.update { it -> !it }
        }
    }

    fun checkRegisterFieldsAreValid(): String {
        return if (_email.value.trim().isEmpty() || _name.value.trim().isEmpty() ||
            _password.value.trim().isEmpty() || _confirmPassword.value.trim().isEmpty()) {
            VALID_ERROR_EMPTY_FIELDS
        } else if (_password.value.trim() != _confirmPassword.value.trim()) {
            VALID_ERROR_PASSWORD_MISMATCH
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            VALID_ERROR_INVALID_EMAIL
        } else CHECKS_PASSED
    }

    fun checkLoginFieldsAreValid(): String {
        return if (_email.value.trim().isEmpty() ||_password.value.trim().isEmpty()) {
            VALID_ERROR_EMPTY_FIELDS
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value.trim()).matches()) {
            VALID_ERROR_INVALID_EMAIL
        } else CHECKS_PASSED
    }

    fun checkIfUsernameExists() {
        viewModelScope.launch(Dispatchers.IO) {
            val usernameExists = loginRepository.doesEmailExist(_email.value.trim())

            updateScreenState(SCREEN_REGISTER_CHECK_EMAIL_DONE)
            assignEmailCheckResponse(usernameExists)
        }
    }

    fun clearInputFields() {
        _password.update { it -> "" }
        _email.update { it -> "" }
        _confirmPassword.update { it -> "" }
        _name.update { it -> "" }
        _showPasswordState.update { it -> false }
        _showConfirmPasswordState.update { it -> false }
    }

    fun makeNewLoginDetails(): LoginDetails {
        return LoginDetails(
            email = _email.value,
            password = _password.value,
            activeUser = false
        )
    }

    fun getLoginDetails(): LoginDetails {
        return if (loginDetails != null) loginDetails!!
            else LoginDetails("", "", false)
    }

    fun createLoginDetails() {
        val newLoginDetails = makeNewLoginDetails()
        loginDetails = newLoginDetails

        viewModelScope.launch(Dispatchers.IO) {
            loginRepository.createUser(newLoginDetails)

            updateScreenState(SCREEN_REGISTER_COMPLETE)
        }
    }

    fun checkLoginDetails() {
        updateScreenState(SCREEN_LOGIN_CHECKING_CREDENTIALS)

        viewModelScope.launch(Dispatchers.IO) {
            val checkResponse = loginRepository.loginUser(_email.value, _password.value)
            println("LoginViewModel: checkLoginDetails: checkResponse = $checkResponse")

            assignCredentialCheckResponse(checkResponse)

            if (checkResponse) {
                updateScreenState(SCREEN_LOGIN_SUCCESS)
                // TODO: Update active user flag to true
            } else {
                updateScreenState(SCREEN_LOGIN_CHECK_DONE)
            }
        }
    }

    suspend fun signIn(request: GetCredentialRequest, context: Context): String? {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Sign In failed!"
        val TAG = "LoginViewModel: signIn:"

        var outputMsg: String? = null

        delay(250)

        try {
            val result = credentialManager.getCredential(request = request, context = context)
            Log.i(TAG, result.toString())
            val credential = result.credential

            if (credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val credential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success")
                            outputMsg = "Sign In Success!"
                            updateScreenState(SCREEN_LOGIN_CHECK_DONE)
                        } else {
                            // If sign in fails, display a message to the user
                            Log.w(TAG, "signInWithCredential:failure", task.exception)
                            outputMsg = "Failed to sign in with Google credentials"
                        }
                    }
            }
        } catch(e: GetCredentialException) {
            outputMsg = "$failureMessage: Failure getting credentials"
            Log.e(TAG, outputMsg, e)
        } catch (e: GoogleIdTokenParsingException) {
            outputMsg = "$failureMessage: Issue with parsing received GoogleIdToken"
            Log.e(TAG, outputMsg, e)
        } catch (e: NoCredentialException) {
            outputMsg = "$failureMessage: No credentials found"
            Log.e(TAG, outputMsg, e)
            return outputMsg
        } catch (e: GetCredentialCustomException) {
            outputMsg = "$failureMessage: Issue with custom credential request"
            Log.e(TAG, outputMsg, e)
        } catch (e: GetCredentialCancellationException) {
            outputMsg = "$failureMessage: Sign-in was cancelled"
            Log.e(TAG, outputMsg, e)
        }
        return outputMsg
    }

    fun handleSignIn(webClientId: String, context: Context) {
        val signInWithGoogleOption: GetSignInWithGoogleOption =
            GetSignInWithGoogleOption.Builder(webClientId)
                .setNonce(generateRandomNonce())
                .build()

        val request: GetCredentialRequest = GetCredentialRequest
            .Builder()
            .addCredentialOption(signInWithGoogleOption)
            .build()

        viewModelScope.launch(Dispatchers.IO) {
            signIn(request, context)
        }
    }

    fun generateRandomNonce(): String {
        val random = SecureRandom()
        val bytes = ByteArray(32) // 32 bytes = 256 bits
        random.nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

    companion object {
        val LOGIN_REPOSITORY_KEY = object : CreationExtras.Key<LoginRepository> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val loginRepository = this[LOGIN_REPOSITORY_KEY] as LoginRepository

                LoginViewModel(loginRepository)
            }
        }
    }
}