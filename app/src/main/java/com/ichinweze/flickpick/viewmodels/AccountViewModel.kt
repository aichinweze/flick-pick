package com.ichinweze.flickpick.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_ERROR_INITIALISING
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.repositiories.AccountRepository
import com.ichinweze.flickpick.repositiories.BaselineRepository
import com.ichinweze.flickpick.repositiories.CsvRepositoryImpl
import com.ichinweze.flickpick.repositiories.LoginRepository
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(
    //val csvRepository: CsvRepositoryImpl,
    //val baselineRepository: BaselineRepository,
    val loginRepository: LoginRepository,
    val accountRepository: AccountRepository
): ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _age: MutableStateFlow<Int> = MutableStateFlow(-1)
    val age = _age.asStateFlow()

    private val questionList = mutableListOf<QuestionData>()
    private val genreChecklistItems = mutableListOf<ChecklistItem>()
    private val movieRegionChecklistItems = mutableListOf<ChecklistItem>()

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            val currentUser = auth.currentUser

            currentUser?.let {
                setAccountName(it.displayName.toString())
                setAccountEmail(it.email.toString())
            }
            println("AccountViewModel: initialiseScreen: name: ${_name.value}")
            println("AccountViewModel: initialiseScreen: email: ${_email.value}")

            _screenState.update { currentState -> SCREEN_INITIALISED }
        }

        /*viewModelScope.launch(Dispatchers.IO) {
            // TODO: Read in baseline details and include at bottom of account page

            if (_screenState.value == SCREEN_UNINITIALISED) {
                val activeUserEmail = loginRepository.getActiveUserEmail()
                println("AccountViewModel: active user email: $activeUserEmail")

                if (activeUserEmail != "") {
                    val accountDetails = accountRepository.getAccountDetails(activeUserEmail)

                    println("AccountViewModel: name = ${accountDetails.name}, email = ${accountDetails.email}")

                    setAccountName(accountDetails.name)
                    setAccountEmail(accountDetails.email)

                    if (accountDetails.age != null) {
                        setAccountAge(accountDetails.age)
                    }

                    _screenState.update { currentState -> SCREEN_INITIALISED }
                    println("AccountViewModel: initialiseScreen: updating screen state to initialised: ${screenState.value}")
                } else {

                    _screenState.update { currentState -> SCREEN_ERROR_INITIALISING }
                    println("AccountViewModel: initialiseScreen: updating screen state to initialised: ${screenState.value}")
                }
            }
        }*/
    }

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    fun updateProfile() {
        userProfileChangeRequest {
            //displayName =
        }
    }

    fun setAccountName(newName: String) {
        _name.update { current -> newName }
    }

    fun setAccountAge(newAge: Int) {
        _age.update { current -> newAge }
    }

    fun setAccountEmail(newEmail: String) {
        _email.update { current -> newEmail }
    }

    companion object {
        //val CSV_REPOSITORY_KEY = object : CreationExtras.Key<CsvRepositoryImpl> {}
        //val BASELINE_REPOSITORY_KEY = object : CreationExtras.Key<BaselineRepository> {}
        val LOGIN_REPOSITORY_KEY = object : CreationExtras.Key<LoginRepository> {}
        val ACCOUNT_REPOSITORY_KEY = object : CreationExtras.Key<AccountRepository> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // val csvRepository = this[CSV_REPOSITORY_KEY] as CsvRepositoryImpl
                // val baselineRepository = this[BASELINE_REPOSITORY_KEY] as BaselineRepository
                val loginRepository = this[LOGIN_REPOSITORY_KEY] as LoginRepository
                val accountRepository = this[ACCOUNT_REPOSITORY_KEY] as AccountRepository

                AccountViewModel(loginRepository, accountRepository)
            }
        }
    }
}