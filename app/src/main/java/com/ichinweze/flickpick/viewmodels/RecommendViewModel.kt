package com.ichinweze.flickpick.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ichinweze.flickpick.data.ApiData.MovieApiResponseData
import com.ichinweze.flickpick.data.ApiData.MovieGenreResponseData
import com.ichinweze.flickpick.data.ApiData.MoviePageResult
import com.ichinweze.flickpick.data.ViewModelData.AUTH_TOKEN
import com.ichinweze.flickpick.data.ViewModelData.BASE_URL
import com.ichinweze.flickpick.data.ViewModelData.MovieQualityData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData
import com.ichinweze.flickpick.data.ViewModelData.ProcessedResult
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISING
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_LOADED_RESULTS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_NO_RESULTS
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_REVIEW_SELECTION
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.data.ViewModelData.TimeBoundData
import com.ichinweze.flickpick.interfaces.MovieApiService
import com.ichinweze.flickpick.repositiories.CsvRepositoryImpl
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.GENRE_LIST_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.MOVIE_QUALITY_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.MOVIE_REGION_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.MOVIE_RUNTIME_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.RECOMMEND_QUESTIONS_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.RELEASE_DECADE_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToGenreData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieQuality
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRegionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToQuestionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToTimeBoundData
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistItem
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistResponse
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.convertGenreToChecklistItem
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.convertMovieRegionToChecklistItem
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.makeIntervalFromLbAndUb
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.collections.mutableMapOf

class RecommendViewModel(val csvRepository: CsvRepositoryImpl): ViewModel() {

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val _checklistOptions: MutableStateFlow<List<ChecklistItem>> = MutableStateFlow(listOf())
    val checklistOptions = _checklistOptions.asStateFlow()

    private val _currentQuestionIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _currentQuestion: MutableStateFlow<String> = MutableStateFlow("")
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _selectedIndex: MutableStateFlow<Int> = MutableStateFlow(-1)
    val selectedIndex = _selectedIndex.asStateFlow()

    private val _searchResults: MutableStateFlow<List<ProcessedResult>> = MutableStateFlow(emptyList())
    val searchResults = _searchResults.asStateFlow()

    private val _movieToReview: MutableStateFlow<List<ProcessedResult>> = MutableStateFlow(emptyList())
    val movieToReview = _movieToReview.asStateFlow()

    private val numberOfQuestions = mutableIntStateOf(0)

    private val numberOfSearchPages: Int = 5

    private val checklistResponseMap = mutableMapOf<Int, ChecklistResponse>()
    private val genreIdMap = mutableMapOf<String, Int>()

    private val questionList = mutableListOf<QuestionData>()
    private val runtimeList = mutableListOf<TimeBoundData>()
    private val decadeList = mutableListOf<TimeBoundData>()
    private val regionList = mutableListOf<MovieRegionData>()

    private val genreChecklistItems = mutableListOf<ChecklistItem>()
    private val movieRegionChecklistItems = mutableListOf<ChecklistItem>()
    private val decadeChecklistItems = mutableListOf<ChecklistItem>()
    private val qualityChecklistItems = mutableListOf<ChecklistItem>()
    private val runtimeChecklistItems = mutableListOf<ChecklistItem>()

    // TODO: Separate out API request to a separate screen/view model??
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Can be any valid base URL
        .addConverterFactory(GsonConverterFactory.create()) // Or your preferred converter
        .build()

    val movieApiService = retrofit.create(MovieApiService::class.java)

    // Read data from csv files and initialise parameters
    fun initialiseScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_screenState.value == SCREEN_UNINITIALISED) {
                println("RecommendViewModel: initialiseScreen: Starting process...")
                updateScreenState(SCREEN_INITIALISING)

                val listOfQuestions = csvRepository
                    .getCsvLines(RECOMMEND_QUESTIONS_CSV, false)
                    .map { mapRawLineToQuestionData(it) }
                questionList.addAll(listOfQuestions)

                val genres = csvRepository
                    .getCsvLines(GENRE_LIST_CSV, false)
                    .map { mapRawLineToGenreData(it) }
                val genreItems = genres.map { convertGenreToChecklistItem(it) }
                genreChecklistItems.addAll(genreItems)

                val movieRegions = csvRepository
                    .getCsvLines(MOVIE_REGION_CSV, false)
                    .map{ mapRawLineToMovieRegionData(it) }
                val movieRegionItems = movieRegions.map { convertMovieRegionToChecklistItem(it) }
                regionList.addAll(movieRegions)
                movieRegionChecklistItems.addAll(movieRegionItems)

                val decades = csvRepository
                    .getCsvLines(RELEASE_DECADE_CSV, false)
                    .map { mapRawLineToTimeBoundData(it) }
                decadeList.addAll(decades)
                val decadeItems = decades.map { convertTimeBoundToChecklistItem(it) }
                decadeChecklistItems.addAll(decadeItems)

                val runtimes = csvRepository
                    .getCsvLines(MOVIE_RUNTIME_CSV, false)
                    .map { mapRawLineToTimeBoundData(it) }
                runtimeList.addAll(runtimes)
                val runtimeItems = runtimes.map { convertTimeBoundToChecklistItem(it) }
                runtimeChecklistItems.addAll(runtimeItems)

                val qualities = csvRepository
                    .getCsvLines(MOVIE_QUALITY_CSV, false)
                    .map { mapRawLineToMovieQuality(it) }
                val qualityItems = qualities.map { convertMovieQualityToChecklistItem(it) }
                qualityChecklistItems.addAll(qualityItems)

                numberOfQuestions.intValue = listOfQuestions.size

                updateChecklistOptions(_currentQuestionIndex.value)
                updateCurrentQuestion(_currentQuestionIndex.value)

                populateGenreIdMap()

                _screenState.update { currentState -> SCREEN_INITIALISED }
                println("RecommendViewModel: initialiseScreen: updating screen state to initialised: ${screenState.value}")
            }
        }
    }

    fun getNumberOfQuestions(): Int {
        return numberOfQuestions.intValue
    }

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    fun updateCurrentQuestion(currQIdx: Int) {
        _currentQuestion.update { currentState ->
            val question = questionList.find { question -> question.index == currQIdx }!!
            question.question
        }
    }

    fun updateChecklistOptions(currQIdx: Int) {
        val checklistOptions =
            when (currQIdx) {
                0       -> genreChecklistItems
                1       -> movieRegionChecklistItems
                2       -> decadeChecklistItems
                3       -> qualityChecklistItems
                else    -> runtimeChecklistItems
            }

        _checklistOptions.update { state -> checklistOptions }
    }

    fun updateSelectedIndex(selectedIdx: Int) {
        _selectedIndex.update { state -> selectedIdx }
    }

    fun updateResponseMap(currQIdx: Int, selectedIdx: Int) {
        val responses = ChecklistResponse(responses = listOf<Int>(selectedIdx))

        checklistResponseMap.put(currQIdx, responses)
    }

    fun emptySearchResults() {
        _searchResults.update { it -> emptyList() }
    }

    fun addToSearchResults(processedResults: List<ProcessedResult>) {
        _searchResults.update { it -> it.toMutableList().plus(processedResults) }
    }

    fun persistChecklistState(currQIdx: Int) {
        val checkedItems = _checklistOptions.value
            .filter { item -> item.isChecked }
            .map { item -> item.index }

        when (currQIdx) {
            0 -> checkedItems.forEach { checkedItemIdx ->
                val checklistItem = genreChecklistItems[checkedItemIdx]
                val updatedChecklistItem = checklistItem.copy(isChecked = true)

                genreChecklistItems[checkedItemIdx] = updatedChecklistItem
            }
            else -> checkedItems.forEach { checkedItemIdx ->
                val checklistItem = movieRegionChecklistItems[checkedItemIdx]
                val updatedChecklistItem = checklistItem.copy(isChecked = true)

                movieRegionChecklistItems[checkedItemIdx] = updatedChecklistItem
            }
        }
    }

    fun goBack() {
        _currentQuestionIndex.update { current ->
            val nextIdx = current - 1
            updateChecklistOptions(nextIdx)
            updateCurrentQuestion(nextIdx)
            updateSelectedIndex(-1)
            nextIdx
        }
    }

    fun goForward() {
        _currentQuestionIndex.update { currentIdx ->
            val nextIdx = currentIdx + 1

            persistChecklistState(currentIdx)
            updateChecklistOptions(nextIdx)
            updateCurrentQuestion(nextIdx)
            updateSelectedIndex(-1)
            nextIdx
        }
    }

    fun convertTimeBoundToChecklistItem(timeBound: TimeBoundData): ChecklistItem {
        return ChecklistItem(
            index = timeBound.index,
            checklistItem = makeIntervalFromLbAndUb(timeBound.timeBoundLb, timeBound.timeBoundUb)
        )
    }

    fun convertMovieQualityToChecklistItem(movieQualityData: MovieQualityData): ChecklistItem {
        return ChecklistItem(
            index = movieQualityData.index,
            checklistItem = movieQualityData.quality
        )
    }

    fun populateGenreIdMap() {
        viewModelScope.launch(Dispatchers.IO) {
            movieApiService
                .getMovieGenreIds(authToken = AUTH_TOKEN)
                .enqueue(object : Callback<MovieGenreResponseData> {
                    override fun onResponse(
                        call: Call<MovieGenreResponseData>,
                        response: Response<MovieGenreResponseData>
                    ) {
                        val responseBody = response.body()

                        if (response.isSuccessful) {
                            if (responseBody != null) {
                                val genreChecklistMap =
                                    genreChecklistItems
                                        .fold(mutableMapOf<String, Int>()) { acc, item ->
                                            val genreItem = item.checklistItem
                                            val genreId = responseBody.genres
                                                .find { it -> it.name == genreItem }

                                            if (genreId != null) {
                                                acc.apply {
                                                    putAll(mapOf(genreItem to genreId.id))
                                                }
                                            } else {
                                                acc
                                            }
                                        }
                                genreIdMap.putAll(genreChecklistMap)
                            } else {
                                // TODO: Better handling
                                println("Recommend View Model: populateGenreIdMap: Response body is empty")
                            }
                        } else {
                            // TODO: Handle API error
                            println("Recommend View Model: populateGenreIdMap: Request threw an error")
                        }
                    }

                    override fun onFailure(call: Call<MovieGenreResponseData>, t: Throwable) {
                        throw(t)
                        // TODO: Add better handling
                    }
                })

        }
    }

    fun constructGenreSearchParameter(): String {
        val genreChecklistResponse = checklistResponseMap.getValue(0).responses.first()
        val genreValue = genreChecklistItems
            .find { item -> item.index == genreChecklistResponse }!!
            .checklistItem
        val genreId = genreIdMap[genreValue]

        return if (genreId != null) "&with_genres=$genreId" else ""
    }

    fun constructRegionSearchParameter(): String {
        val regionChecklistResponse = checklistResponseMap.getValue(1).responses.first()
        val regionValue = regionList
            .find { item -> item.index == regionChecklistResponse }!!
            .country

        return "&with_origin_country=$regionValue"
    }

    fun constructReleaseDecadeSearchParameters(): String {
        val checklistResponse = checklistResponseMap.getValue(2).responses.first()
        val timeBoundValue = decadeList.find { item -> item.index == checklistResponse }

        if (timeBoundValue != null) {
            val lb =
                if (timeBoundValue.timeBoundLb == "" || timeBoundValue.timeBoundLb == "_") ""
                else "&release_date.gte=${timeBoundValue.timeBoundLb}-01-01"

            val ub =
                if (timeBoundValue.timeBoundUb == "" || timeBoundValue.timeBoundUb == "_") ""
                else "&release_date.lte=${timeBoundValue.timeBoundUb}-12-31"

            return lb + ub
        } else {
            return ""
        }
    }

    fun constructRuntimeSearchParameters(): String {
        val checklistResponse = checklistResponseMap.getValue(2).responses.first()
        val timeBoundValue = runtimeList.find { item -> item.index == checklistResponse }

        if (timeBoundValue != null) {
            val lb =
                if (timeBoundValue.timeBoundLb == "" || timeBoundValue.timeBoundLb == "_") ""
                else "&with_runtime.gte=${timeBoundValue.timeBoundLb}"

            val ub =
                if (timeBoundValue.timeBoundUb == "" || timeBoundValue.timeBoundUb == "_") ""
                else "&with_runtime.lte=${timeBoundValue.timeBoundUb}"

            return lb + ub
        } else {
            return ""
        }
    }

    fun constructQualitySearchParameters(): String {
        val checklistResponse = checklistResponseMap.getValue(3).responses.first()

        return when(checklistResponse) {
            0       -> "&vote_average.gte=0&vote_average.lte=5"
            1       -> "&vote_average.gte=5&vote_average.lte=6"
            2       -> "&vote_average.gte=6&vote_average.lte=7"
            3       -> "&vote_average.gte=7&vote_average.lte=8.5"
            else    -> "&vote_average.gte=8.5"
        }
    }

    fun processResult(apiResponseData: MoviePageResult): ProcessedResult {
        return ProcessedResult(
            id = apiResponseData.id,
            genreIds = apiResponseData.genreIds,
            title = apiResponseData.title,
            voteAverage = apiResponseData.voteAverage,
            voteCount = apiResponseData.voteCount,
            popularity = apiResponseData.popularity,
            posterPath = apiResponseData.posterPath,
            releaseDate = apiResponseData.releaseDate,
            overview = apiResponseData.overview
        )
    }

    fun searchMovieApiForResults() {
        val discoverPrefix = "discover/movie?include_adult=false&include_video=false&language=en-US&sort_by=popularity.desc"

        val withGenres = constructGenreSearchParameter()
        val withRegion = constructRegionSearchParameter()
        val withReleasePeriod = constructReleaseDecadeSearchParameters()
        val withQuality = constructQualitySearchParameters()
        val withRuntime = constructRuntimeSearchParameters()

        val pageRange = (1..numberOfSearchPages).toList()
        emptySearchResults()

        for (page in pageRange) {
            val searchUrl = "$discoverPrefix&page=$page$withGenres$withRegion$withReleasePeriod$withQuality$withRuntime"
            println("searchMovieApiForResults: page search URL: $searchUrl")

            viewModelScope.launch(Dispatchers.IO) { movieApiService
                .getMovieApiResponse(AUTH_TOKEN, urlWithSearchParams = searchUrl)
                .enqueue(object : Callback<MovieApiResponseData> {
                    override fun onResponse(
                        call: Call<MovieApiResponseData>,
                        response: Response<MovieApiResponseData>
                    ) {
                        val responseBody = response.body()

                        if (response.isSuccessful) {
                            if (responseBody != null) {
                                val results = responseBody.results.map { processResult(it) }

                                if (results.isNotEmpty()) {
                                    addToSearchResults(results)
                                }

                                if (page == pageRange.last()) {
                                    if (responseBody.totalResults == 0) {
                                        updateScreenState(SCREEN_NO_RESULTS)
                                        print("Recommend View Model: searchMovieApiForResults: No results, reflecting this on the screen...")
                                    } else {
                                        updateScreenState(SCREEN_LOADED_RESULTS)
                                        print("Recommend View Model: searchMovieApiForResults: Final iteration at page $page...")
                                    }
                                }

                                println("Recommend View Model: searchMovieApiForResults: acquired results: $results")
                                println("Recommend View Model: searchMovieApiForResults: updated list: ${_searchResults.value}")
                                println("Recommend View Model: searchMovieApiForResults: total pages = ${responseBody.totalPages}, total results = ${responseBody.totalResults}")
                            } else {
                                // TODO: Better handling
                                println("Recommend View Model: searchMovieApiForResults: Response body is empty")
                            }
                        } else {
                            // TODO: Handle API error
                            println("Recommend View Model: searchMovieApiForResults: Request threw an error")
                        }
                    }

                    override fun onFailure(call: Call<MovieApiResponseData>, t: Throwable) {
                        throw(t)
                        // TODO: Add better handling
                    }
                })
            }
        }
    }

    fun assignMovieToReview(processedResult: ProcessedResult) {
        _movieToReview.update { listOfMovies -> listOf(processedResult) }
        updateScreenState(SCREEN_REVIEW_SELECTION)
    }

    fun unselectMovieForReview() {
        updateScreenState(SCREEN_LOADED_RESULTS)

        _movieToReview.update { listOfMovies -> emptyList() }
    }

    fun selectMovie() {
        // TODO: Persist information acquired from questionnaire
        // TODO: When questions are done, should see button to return to dashboard
        resetScreen()
    }

    fun resetScreen() {
        println("Recommend View Model: Complete Response Map: $checklistResponseMap")
        updateScreenState(SCREEN_UNINITIALISED)
        emptySearchResults()

        numberOfQuestions.intValue = 0
        _checklistOptions.update { current -> listOf() }
        _currentQuestionIndex.update { current -> 0 }
        _currentQuestion.update { current -> "" }
        _selectedIndex.update { state -> -1 }

        checklistResponseMap.clear()
        genreIdMap.clear()
        questionList.clear()
        runtimeList.clear()
        decadeList.clear()
        regionList.clear()

        genreChecklistItems.clear()
        movieRegionChecklistItems.clear()
        decadeChecklistItems.clear()
        qualityChecklistItems.clear()
        runtimeChecklistItems.clear()
    }

    companion object {
        val CSV_REPOSITORY_KEY = object : CreationExtras.Key<CsvRepositoryImpl> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val csvRepository = this[CSV_REPOSITORY_KEY] as CsvRepositoryImpl
                RecommendViewModel(csvRepository)
            }
        }
    }
}