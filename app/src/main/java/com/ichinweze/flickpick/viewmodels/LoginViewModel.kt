package com.ichinweze.flickpick.viewmodels

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOGIN_CHECK_DONE
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.security.SecureRandom

class LoginViewModel(): ViewModel() {

    private val auth = FirebaseAuth.getInstance()

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _name: MutableStateFlow<String> = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val TAG: String = "LoginViewModel"

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    suspend fun signIn(request: GetCredentialRequest, context: Context): String? {
        val credentialManager = CredentialManager.create(context)
        val failureMessage = "Sign In failed!"

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
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                LoginViewModel()
            }
        }
    }
}