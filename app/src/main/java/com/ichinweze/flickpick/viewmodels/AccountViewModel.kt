package com.ichinweze.flickpick.viewmodels

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.firestore
import com.ichinweze.flickpick.data.ViewModelData.ACCOUNT_DETAILS_COLLECTION
import com.ichinweze.flickpick.data.ViewModelData.BASELINE_QUESTIONS_COLLECTION
import com.ichinweze.flickpick.data.ViewModelData.QUESTIONS_COLLECTION
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGOUT_SUCCESS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.data.ViewModelData.UNINITIALISED_AGE
import com.ichinweze.flickpick.data.ViewModelData.UNINITIALISED_NAME
import com.ichinweze.flickpick.data.firestore.BaselineQuestion
import com.ichinweze.flickpick.data.firestore.UserAccountDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountViewModel(): ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val firestoreDb = Firebase.firestore

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _previousUserDetails: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())

    private val _baselineQuestions: MutableStateFlow<List<BaselineQuestion>> = MutableStateFlow(emptyList())
    val baselineQuestions = _baselineQuestions.asStateFlow()

    private val _name: MutableStateFlow<String> = MutableStateFlow(UNINITIALISED_NAME)
    val name = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _age: MutableStateFlow<String> = MutableStateFlow(UNINITIALISED_AGE)
    val age = _age.asStateFlow()

    private val TAG: String = "AccountViewModel"

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            val currentUser = auth.currentUser

            currentUser?.let {
                setAccountName(it.displayName.toString())
                setAccountEmail(it.email.toString())
            }

            getUserDetailsFromFirestore()

            getBaselineQuestionsFromFirestore()

            _screenState.update { currentState -> SCREEN_INITIALISED }
        }
    }

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
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

    fun getUserDetailsFromFirestore() {
        val docRef = firestoreDb.collection(ACCOUNT_DETAILS_COLLECTION).document(_email.value)

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
                updateScreenState(SCREEN_INITIALISED)
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
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
                .collection(ACCOUNT_DETAILS_COLLECTION)
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

    fun getBaselineQuestionsFromFirestore() {
        val collectionPath = "$BASELINE_QUESTIONS_COLLECTION/${_email.value}/$QUESTIONS_COLLECTION"
        val collectionRef = firestoreDb.collection(collectionPath)

        collectionRef
            .get()
            .addOnSuccessListener { result ->
                for (documentSnapshot in result) {
                    val baselineQuestion = documentSnapshot.toObject(BaselineQuestion::class.java)
                    Log.d(TAG, "Baseline question with index ${baselineQuestion.questionIndex} retrieved")

                    _baselineQuestions.update { currentList -> currentList.plus(baselineQuestion) }
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
            }
    }

    fun resetScreen() {
        _screenState.update { state -> SCREEN_UNINITIALISED }
        _previousUserDetails.update { state -> emptyMap() }
        _baselineQuestions.update { currentList -> emptyList() }
        _name.update { state -> UNINITIALISED_NAME }
        _age.update { state -> UNINITIALISED_AGE }
        _email.update { state -> "" }
    }

    fun signOutUser(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            auth.signOut()

            val credentialManager = CredentialManager.create(context)

            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            _screenState.update { state -> SCREEN_LOGOUT_SUCCESS }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AccountViewModel()
            }
        }
    }
}