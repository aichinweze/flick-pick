package com.ichinweze.flickpick.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ichinweze.flickpick.data.ViewModelData.MovieQualityData
import com.ichinweze.flickpick.data.ViewModelData.MovieRuntimeData
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.ReleaseDecadeData
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISING
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
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
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRuntime
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToQuestionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToReleaseDecade
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

    private val questionList = mutableListOf<QuestionData>()

    private val checklistResponseMap = mutableMapOf<Int, ChecklistResponse>()

    private val genreChecklistItems = mutableListOf<ChecklistItem>()
    private val movieRegionChecklistItems = mutableListOf<ChecklistItem>()
    private val decadeChecklistItems = mutableListOf<ChecklistItem>()
    private val qualityChecklistItems = mutableListOf<ChecklistItem>()
    private val runtimeChecklistItems = mutableListOf<ChecklistItem>()

    private val numberOfQuestions = mutableIntStateOf(0)

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
                println("RecommendViewModel: initialiseScreen: list of questions assigned to ViewModel: $questionList")

                val genres = csvRepository
                    .getCsvLines(GENRE_LIST_CSV, false)
                    .map { mapRawLineToGenreData(it) }
                val genreItems = genres.map { convertGenreToChecklistItem(it) }
                genreChecklistItems.addAll(genreItems)
                println("RecommendViewModel: initialiseScreen: genre checklist assigned to ViewModel: $genreChecklistItems")

                val movieRegions = csvRepository
                    .getCsvLines(MOVIE_REGION_CSV, false)
                    .map{ mapRawLineToMovieRegionData(it) }
                val movieRegionItems = movieRegions.map { convertMovieRegionToChecklistItem(it) }
                movieRegionChecklistItems.addAll(movieRegionItems)
                println("RecommendViewModel: initialiseScreen: movie region checklist assigned to ViewModel: $movieRegionChecklistItems")

                val decades = csvRepository
                    .getCsvLines(RELEASE_DECADE_CSV, false)
                    .map { mapRawLineToReleaseDecade(it) }
                val decadeItems = decades.map { convertReleaseDecadeToChecklistItem(it) }
                decadeChecklistItems.addAll(decadeItems)
                println("RecommendViewModel: initialiseScreen: decade checklist assigned to ViewModel: $decadeChecklistItems")

                val runtimes = csvRepository
                    .getCsvLines(MOVIE_RUNTIME_CSV, false)
                    .map { mapRawLineToMovieRuntime(it) }
                val runtimeItems = runtimes.map { convertMovieRuntimeToChecklistItem(it) }
                runtimeChecklistItems.addAll(runtimeItems)
                println("RecommendViewModel: initialiseScreen: movie runtime checklist assigned to ViewModel: $runtimeChecklistItems")

                val qualities = csvRepository
                    .getCsvLines(MOVIE_QUALITY_CSV, false)
                    .map { mapRawLineToMovieQuality(it) }
                val qualityItems = qualities.map { convertMovieQualityToChecklistItem(it) }
                qualityChecklistItems.addAll(qualityItems)
                println("RecommendViewModel: initialiseScreen: movie quality checklist assigned to ViewModel: $runtimeChecklistItems")

                numberOfQuestions.intValue = listOfQuestions.size
                println("RecommendViewModel: initialiseScreen: assigning size of questions to VM: ${numberOfQuestions.intValue}")

                updateChecklistOptions(_currentQuestionIndex.value)
                println("RecommendViewModel: initialiseScreen: getting initial set of checklist options: ${checklistOptions.value}")

                updateCurrentQuestion(_currentQuestionIndex.value)
                println("RecommendViewModel: initialiseScreen: assigned current question: ${_currentQuestion.value}")

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

    fun resetScreen() {
        println("Recommend View Model: Complete Response Map: $checklistResponseMap")
        updateScreenState(SCREEN_UNINITIALISED)

        numberOfQuestions.intValue = 0
        _checklistOptions.update { current -> listOf() }
        _currentQuestionIndex.update { current -> 0 }
        _currentQuestion.update { current -> "" }
        _selectedIndex.update { state -> -1 }
    }

    fun convertReleaseDecadeToChecklistItem(rDecadeData: ReleaseDecadeData): ChecklistItem {
        return ChecklistItem(
            index = rDecadeData.index,
            checklistItem = makeIntervalFromLbAndUb(rDecadeData.decadeLb, rDecadeData.decadeUb)
        )
    }

    fun convertMovieRuntimeToChecklistItem(mRuntime: MovieRuntimeData): ChecklistItem {
        return ChecklistItem(
            index = mRuntime.index,
            checklistItem = makeIntervalFromLbAndUb(mRuntime.runtimeLb, mRuntime.runtimeUb)
        )
    }

    fun convertMovieQualityToChecklistItem(movieQualityData: MovieQualityData): ChecklistItem {
        return ChecklistItem(
            index = movieQualityData.index,
            checklistItem = movieQualityData.quality
        )
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