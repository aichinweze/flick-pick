package com.ichinweze.flickpick.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavController
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
import com.ichinweze.flickpick.screens.utils.DASHBOARD_SCREEN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(val loginRepository: LoginRepository): ViewModel() {

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

    private var loginDetails: LoginDetails? = null

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            println("LoginViewModel: initialiseScreen: initialising screen...")
            viewModelScope.launch(Dispatchers.IO) {
                val activeUserExists = loginRepository.findActiveUser()

                updateScreenState(SCREEN_INITIALISED)

                // TODO: Move to Main screen?
                if (activeUserExists) {
                    // proceed to dashboard with active user
                } else {
                    // proceed to sign up or register
                }
            }
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

    fun checkFieldsAreValid(): String {
        return if (_email.value.trim().isEmpty() || _name.value.trim().isEmpty() ||
            _password.value.trim().isEmpty() || _confirmPassword.value.trim().isEmpty()) {
            VALID_ERROR_EMPTY_FIELDS
        } else if (_password.value.trim() != _confirmPassword.value.trim()) {
            VALID_ERROR_PASSWORD_MISMATCH
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
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
            val checkResponse = loginRepository.findUser(_email.value, _password.value)
            println("LoginViewModel: checkLoginDetails: checkResponse = $checkResponse")

            assignCredentialCheckResponse(checkResponse)

            if (checkResponse) {
                updateScreenState(SCREEN_LOGIN_SUCCESS)
            } else {
                updateScreenState(SCREEN_LOGIN_CHECK_DONE)
            }
        }
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