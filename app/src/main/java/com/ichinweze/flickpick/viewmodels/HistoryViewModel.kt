package com.ichinweze.flickpick.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.firestore
import com.ichinweze.flickpick.data.ViewModelData.MOVIE_DETAILS_COLLECTION
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_EMPTY_HISTORY
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REVIEW_SELECTION
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.data.ViewModelData.WATCH_HISTORY_COLLECTION
import com.ichinweze.flickpick.data.firestore.SelectedMovieDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HistoryViewModel(): ViewModel() {
    private val auth = FirebaseAuth.getInstance()

    private val firestoreDb = Firebase.firestore

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _previouslySelectedMovies: MutableStateFlow<List<SelectedMovieDetails>> =
        MutableStateFlow(emptyList())
    val previouslySelectedMovieDetails = _previouslySelectedMovies.asStateFlow()

    private val _email: MutableStateFlow<String> = MutableStateFlow("")

    private val _uid: MutableStateFlow<String> = MutableStateFlow("")

    private val _movieUnderReviewIdx: MutableStateFlow<Int> = MutableStateFlow(-1)

    private var _movieUnderReview: SelectedMovieDetails? = null

    private val _previousRatingToTrack: MutableStateFlow<Int> = MutableStateFlow(-1)

    private val _sliderPosition: MutableStateFlow<Float> = MutableStateFlow(0f)
    val sliderPosition = _sliderPosition.asStateFlow()

    private val TAG: String = "HistoryViewModel: "

    fun initialiseScreen() {
        if (_screenState.value == SCREEN_UNINITIALISED) {
            resetScreen()

            val currentUser = auth.currentUser

            currentUser?.let {
                setAccountEmail(it.email.toString())
                setAccountUid(it.uid)
            }

            getPreviouslySelectedMovies()
        }
    }

    fun setAccountEmail(newEmail: String) {
        _email.update { current -> newEmail }
    }

    fun setAccountUid(uid: String) {
        _uid.update { current -> uid }
    }

    fun getPreviouslySelectedMovies() {
        val collectionPath = "$WATCH_HISTORY_COLLECTION/${_uid.value}/$MOVIE_DETAILS_COLLECTION"
        val collectionRef = firestoreDb.collection(collectionPath)

        collectionRef.count()
            .get(AggregateSource.SERVER)
            .addOnSuccessListener { result ->
                val count = result.count

                if (count == 0L) {
                    Log.d(TAG, "The collection at $collectionPath is empty.")
                    updateScreenState(SCREEN_EMPTY_HISTORY)
                } else {
                    collectionRef
                        .get()
                        .addOnSuccessListener { result ->
                            val selectedMovies = result
                                .map { docSnapshot ->
                                    val selectedMovie = docSnapshot.toObject(SelectedMovieDetails::class.java)
                                    Log.d(TAG, "Movie with index ${selectedMovie.movieId} retrieved")
                                    selectedMovie
                                }
                                .sortedBy { it -> it.movieTitle }

                            _previouslySelectedMovies.update { currentList -> selectedMovies }
                            updateScreenState(SCREEN_INITIALISED)
                        }
                        .addOnFailureListener { exception ->
                            Log.d(TAG, "Error getting documents: ", exception)
                        }
                }
            }
    }

    fun reviewMovieRating(movieIdx: Int) {
        _movieUnderReviewIdx.update { state -> movieIdx }
        _movieUnderReview = _previouslySelectedMovies.value[_movieUnderReviewIdx.value]
        _sliderPosition.update { value ->
            if (_movieUnderReview?.userRating != null) _movieUnderReview!!.userRating!!.toFloat()
            else 0f
        }
        _screenState.update { state -> SCREEN_REVIEW_SELECTION }
    }

    fun getMovieUnderReview(): SelectedMovieDetails {
        return _movieUnderReview!!
    }

    fun setDetailsToTrack() {
        if (_movieUnderReview?.userRating == null) _previousRatingToTrack.update { value -> -1 }
        else _previousRatingToTrack.update { value -> _movieUnderReview?.userRating!! }
    }

    fun updateScreenState(newState: String) {
        _screenState.update { state -> newState }
    }

    fun setSliderPosition(newState: Float) {
        _sliderPosition.update { state -> newState }
    }

    fun updateMovieHistoryList() {
        _previouslySelectedMovies.update { movies ->
            val mutableMovies = movies.toMutableList()

            mutableMovies[_movieUnderReviewIdx.value] = _movieUnderReview!!

            mutableMovies.toList()
        }
    }

    fun updateMovieEntry() {
        if (sliderPosition.value.toInt() != _previousRatingToTrack.value) {
            // update firestore doc
            val updatedMovieEntry = _movieUnderReview!!.copy(userRating = _sliderPosition.value.toInt())
            val movie = _movieUnderReview!!.movieTitle ?: ""

            val documentPath = "$WATCH_HISTORY_COLLECTION/${_uid.value}/$MOVIE_DETAILS_COLLECTION/$movie"
            val docUpdateRef = firestoreDb.document(documentPath)

            docUpdateRef.set(updatedMovieEntry)
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully written with ID: ${updatedMovieEntry.movieTitle} to $WATCH_HISTORY_COLLECTION/${_uid.value}/movie_details")
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

            _movieUnderReview = updatedMovieEntry
            updateMovieHistoryList()
        }

        _screenState.update { state -> SCREEN_REVIEW_SELECTION }
    }

    fun deleteMovieFromHistory() {
        if (auth.currentUser == null) return
        else {
            val movie = _movieUnderReview!!.movieTitle ?: ""

            val documentPath = "$WATCH_HISTORY_COLLECTION/${_uid.value}/$MOVIE_DETAILS_COLLECTION/$movie"
            val docDeleteRef = firestoreDb.document(documentPath)

            docDeleteRef
                .delete()
                .addOnSuccessListener {
                    Log.d(TAG, "DocumentSnapshot successfully deleted from path: $documentPath!")
                }
                .addOnFailureListener { e -> Log.w(TAG, "Error deleting document", e) }

            getPreviouslySelectedMovies()
        }
    }

    fun resetScreen() {
        _movieUnderReviewIdx.update { state -> -1 }
        _movieUnderReview = null
        _email.update { value -> "" }
        _previousRatingToTrack.update { value -> -1 }
        _sliderPosition.update { value -> 0f }
        _previouslySelectedMovies.update { state -> emptyList() }
        _screenState.update { state -> SCREEN_UNINITIALISED }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HistoryViewModel()
            }
        }
    }
}