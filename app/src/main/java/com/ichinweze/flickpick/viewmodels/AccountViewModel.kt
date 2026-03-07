package com.ichinweze.flickpick.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.data.firestore.UserAccountDetails
import com.ichinweze.flickpick.repositiories.AccountRepository
import com.ichinweze.flickpick.repositiories.LoginRepository
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AccountViewModel(
    //val csvRepository: CsvRepositoryImpl,
    //val baselineRepository: BaselineRepository,
    val loginRepository: LoginRepository,
    val accountRepository: AccountRepository
): ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val firestoreDb = Firebase.firestore

    private val _selectedNavBarState: MutableStateFlow<Int> = MutableStateFlow(0)
    val selectedNavBarState = _selectedNavBarState.asStateFlow()

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _previousUserDetails: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    val previousUserDetails = _previousUserDetails.asStateFlow()

    private val _name: MutableStateFlow<String> = MutableStateFlow("Please provide name")
    val name = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _age: MutableStateFlow<String> = MutableStateFlow("Please provide age")
    val age = _age.asStateFlow()

    private val questionList = mutableListOf<QuestionData>()
    private val genreChecklistItems = mutableListOf<ChecklistItem>()
    private val movieRegionChecklistItems = mutableListOf<ChecklistItem>()

    private val TAG: String = "AccountViewModel"

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            val currentUser = auth.currentUser

            currentUser?.let {
                setAccountName(it.displayName.toString())
                setAccountEmail(it.email.toString())
            }
            println("AccountViewModel: initialiseScreen: name: ${_name.value}")
            println("AccountViewModel: initialiseScreen: email: ${_email.value}")

            // TODO: Get details from Firestore for user
            val docRef = firestoreDb.collection("account_details").document(_email.value)

            docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val user = documentSnapshot.toObject(UserAccountDetails::class.java)
                        Log.d(TAG, "User details successfully retrieved")

                        if (user != null) {
                            setAccountAge(user.age ?: "")
                        }
                    } else {
                        Log.d(TAG, "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d(TAG, "get failed with ", exception)
                }

            _screenState.update { currentState -> SCREEN_INITIALISED }
        }
    }

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    fun updateSelectedNavBarState(newState: Int) {
        _selectedNavBarState.update { currentState -> newState }
    }

    fun setAccountName(newName: String) {
        _name.update { current -> newName }
    }

    fun setAccountAge(newAge: String) {
        _age.update { current -> newAge }
    }

    fun setAccountEmail(newEmail: String) {
        _email.update { current -> newEmail }
    }

    fun setPreviousUserDetailsToTrack() {
        val detailsToTrack = mapOf(
            "name" to _name.value,
            "age" to _age.value
        )

        _previousUserDetails.update { current -> detailsToTrack }
    }

    // TODO: Add security layer
    fun updateUserInformationInFirestore() {
        val originalName = _previousUserDetails.value.getValue("name").trim()
        val originalAge = _previousUserDetails.value.getValue("age").trim()

        val newName = _name.value.trim()
        val newAge = _age.value.toString().trim()

        // update firestore DB
        if ((originalName != newName) || (originalAge != newAge)) {
            val updatedUser = UserAccountDetails(name = newName, age = newAge)

            firestoreDb
                .collection("account_details")
                .document(_email.value)
                .set(updatedUser)
                .addOnSuccessListener {
                    // Handle success (e.g., show a Toast message)
                    Log.d(TAG, "DocumentSnapshot successfully written with ID: ${_email.value}")
                }
                .addOnFailureListener { e ->
                    // Handle failure (e.g., log the error)
                    Log.w(TAG, "Error writing document", e)
                }
        }

        // update firebase auth
        if (originalName != newName) {
            // update firebase auth profile
            val profileUpdates = userProfileChangeRequest { displayName = newName }
            val user = auth.currentUser

            user!!.updateProfile(profileUpdates)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "User profile updated.")
                    }
                }

        }
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