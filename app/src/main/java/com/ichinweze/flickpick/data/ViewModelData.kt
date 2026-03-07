package com.ichinweze.flickpick.data

object ViewModelData {

    const val SCREEN_UNINITIALISED = "screen_uninitialised"
    const val SCREEN_INITIALISING = "screen_initialising"
    const val SCREEN_ERROR_INITIALISING = "screen_error_initialising"
    const val SCREEN_INITIALISED = "screen_initialised"
    const val SCREEN_LOADING_RESULTS = "screen_loading_results"
    const val SCREEN_LOADED_RESULTS = "screen_loaded_results"
    const val SCREEN_REVIEW_SELECTION = "screen_review_selection"
    const val SCREEN_NO_RESULTS = "screen_no_results"

    const val SCREEN_EDIT_MODE = "screen_edit_mode"

    const val SCREEN_LOGIN = "screen_login"
    const val SCREEN_LOGIN_CHECKING_CREDENTIALS = "screen_login_checking_credentials"
    const val SCREEN_LOGIN_CHECK_DONE = "screen_login_check_done"
    const val SCREEN_LOGIN_SUCCESS = "screen_login_success"
    const val SCREEN_LOGOUT_SUCCESS = "screen_logout_success"

    const val SCREEN_REGISTER = "screen_register"
    const val SCREEN_REGISTER_CHECKING_EMAIL = "screen_register_checking_email"
    const val SCREEN_REGISTER_CHECK_EMAIL_DONE = "screen_register_check_email_done"
    const val SCREEN_REGISTER_COMPLETE = "screen_register_complete"

    const val PASSWORD_FIELD = "password_field"
    const val CONFIRM_PASSWORD_FIELD = "confirm_password_field"

    const val VALID_ERROR_NO_USER = "No user exists matching these credentials."
    const val VALID_ERROR_EMPTY_FIELDS = "There are empty fields in your registration. Please fill them."
    const val VALID_ERROR_PASSWORD_MISMATCH = "Passwords do not match."
    const val VALID_ERROR_EMAIL_EXISTS = "User with this email is already present in the system."
    const val VALID_ERROR_INVALID_EMAIL = "Please provide a valid email."
    const val CHECKS_PASSED = "checks_passed"

    const val UNINITIALISED_NAME = "Please provide name"
    const val UNINITIALISED_AGE = "Please provide age"

    const val BASE_URL = "https://api.themoviedb.org/3/"

    const val AUTH_TOKEN = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI5MDBmZGZlNGY2OWY3MWY5YzFkNzJiNTAwZDBmMDE3OSIsIm5iZiI6MTc1OTU5OTE0OC4yNjIsInN1YiI6IjY4ZTE1YTJjYTIyNjYyN2FjZjI1YWIzZSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.ijkX6Eg1pTo-lZUChSjigXDTbl7ei33bUNtzqu6mFOA"

    data class QuestionData(
        val index: Int,
        val question: String,
        val isOptional: Boolean
    )

    data class GenreData(val index: Int, val genre: String)

    data class MovieRegionData(val index: Int, val industryName: String, val country: String)

    data class TimeBoundData(
        val index: Int,
        val timeBoundLb: String,
        val timeBoundUb: String
    )

    data class MovieQualityData(
        val index: Int,
        val quality: String
    )

    data class ProcessedResult(
        val id: Int,
        val genreIds: List<Int>,
        val title: String,
        val voteAverage: Float,
        val voteCount: Int,
        val popularity: Float,
        val posterPath: String?,
        val releaseDate: String,
        val overview: String
    )

    val emptyProcessedResult = ProcessedResult(
        id = 0,
        genreIds = emptyList(),
        title = "",
        voteAverage = 0f,
        voteCount = 0,
        popularity = 0f,
        posterPath = null,
        releaseDate = "",
        overview = ""
    )
}