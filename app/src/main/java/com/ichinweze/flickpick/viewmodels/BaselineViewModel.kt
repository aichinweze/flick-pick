package com.ichinweze.flickpick.viewmodels

import androidx.compose.runtime.mutableIntStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISED
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_INITIALISING
import com.ichinweze.flickpick.data.ViewModelData.SCREEN_UNINITIALISED
import com.ichinweze.flickpick.repositiories.CsvRepositoryImpl
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.BASELINE_QUESTIONS_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.GENRE_LIST_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.MOVIE_REGION_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToQuestionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToGenreData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRegionData
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistItem
import com.ichinweze.flickpick.viewmodels.utils.ViewModelUtils.ChecklistResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BaselineViewModel(val csvRepository: CsvRepositoryImpl) : ViewModel() {

    private val _screenState: MutableStateFlow<String> = MutableStateFlow(SCREEN_UNINITIALISED)
    val screenState = _screenState.asStateFlow()

    private val questionList = mutableListOf<QuestionData>()

    private val genreChecklistItems = mutableListOf<ChecklistItem>()
    private val movieRegionChecklistItems = mutableListOf<ChecklistItem>()

    private val numberOfQuestions = mutableIntStateOf(0)

    private val checklistResponseMap = mutableMapOf<Int, ChecklistResponse>()

    private val _checklistOptions: MutableStateFlow<List<ChecklistItem>> = MutableStateFlow(listOf())
    val checklistOptions = _checklistOptions.asStateFlow()
            
    private val _currentQuestionIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _currentQuestion: MutableStateFlow<String> = MutableStateFlow("")
    val currentQuestion = _currentQuestion.asStateFlow()

    private val _currentQuestionIsOptional: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val currentQuestionIsOptional = _currentQuestionIsOptional.asStateFlow()

    // Read questions read from csv
    fun initialiseScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            if (_screenState.value == SCREEN_UNINITIALISED) {
                println("BaselineViewModel: initialiseScreen: Starting process...")
                updateScreenState(SCREEN_INITIALISING)

                val listOfQuestions = csvRepository
                    .getCsvLines(BASELINE_QUESTIONS_CSV, false)
                    .map { mapRawLineToQuestionData(it) }

                questionList.addAll(listOfQuestions)
                println("BaselineViewModel: initialiseScreen: list of questions assigned to ViewModel: $questionList")

                val genres = csvRepository
                    .getCsvLines(GENRE_LIST_CSV, false)
                    .map { mapRawLineToGenreData(it) }
                val genreItems = genres.map { genre -> convertGenreToChecklistItem(genre) }

                genreChecklistItems.addAll(genreItems)
                println("BaselineViewModel: initialiseScreen: genre checklist assigned to ViewModel: $genreChecklistItems")

                val movieRegions = csvRepository
                    .getCsvLines(MOVIE_REGION_CSV, false)
                    .map{ mapRawLineToMovieRegionData(it) }
                val movieRegionItems = movieRegions.map { region -> convertMovieRegionToChecklistItem(region) }

                // TODO: Get any existing information from database when available

                movieRegionChecklistItems.addAll(movieRegionItems)
                println("BaselineViewModel: initialiseScreen: movie region checklist assigned to ViewModel: $movieRegionChecklistItems")

                numberOfQuestions.intValue = listOfQuestions.size
                println("BaselineViewModel: initialiseScreen: assigning size of questions to VM: ${numberOfQuestions.intValue}")

                updateChecklistOptions(_currentQuestionIndex.value)
                println("BaselineViewModel: initialiseScreen: getting initial set of checklist options: ${checklistOptions.value}")

                updateCurrentQuestion(_currentQuestionIndex.value)
                println("BaselineViewModel: initialiseScreen: assigned current question: ${_currentQuestion.value}")

                _screenState.update { currentState -> SCREEN_INITIALISED }
                println("BaselineViewModel: initialiseScreen: updating screen state to initialised: ${screenState.value}")
            }
        }
    }

    // Getters
    fun getNumberOfQuestions(): Int {
        return numberOfQuestions.intValue
    }

    fun makeMovieRegionOption(movieRegionData: MovieRegionData): String {
        val bracketedSuffix = 
            if (movieRegionData.industryName != "") "(${movieRegionData.industryName})"
            else ""
        
        return "${movieRegionData.country} $bracketedSuffix".trim()
    }

    fun convertGenreToChecklistItem(genreData: GenreData): ChecklistItem {
        return ChecklistItem(
            index = genreData.index,
            checklistItem = genreData.genre
        )
    }

    fun convertMovieRegionToChecklistItem(movieRegionData: MovieRegionData): ChecklistItem {
        return ChecklistItem(
            index = movieRegionData.index,
            checklistItem = makeMovieRegionOption(movieRegionData)
        )
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
                else    -> movieRegionChecklistItems
            }

        _checklistOptions.update { state -> checklistOptions }
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

    fun updateScreenState(newState: String) {
        _screenState.update { currentState -> newState }
    }

    fun goBack() {
        _currentQuestionIndex.update { current ->
            val nextIdx = current - 1
            updateChecklistOptions(nextIdx)
            updateCurrentQuestion(nextIdx)
            nextIdx
        }
    }

    fun goForward() {
        _currentQuestionIndex.update { currentIdx ->
            val nextIdx = currentIdx + 1

            persistChecklistState(currentIdx)
            updateChecklistOptions(nextIdx)
            updateCurrentQuestion(nextIdx)
            nextIdx
        }
    }

    fun resetScreen() {
        _screenState.update { current -> SCREEN_UNINITIALISED }

        numberOfQuestions.intValue = 0
        _checklistOptions.update { current -> listOf() }
        _currentQuestionIndex.update { current -> 0 }
        _currentQuestion.update { current -> "" }
    }

    fun updateResponseMap(currQIdx: Int, checklistItems: List<ChecklistItem>) {
        val responses = ChecklistResponse(responses = checklistItems.map { item -> item.index })

        checklistResponseMap.put(currQIdx, responses)
    }

    fun getResponseMap(): MutableSet<MutableMap.MutableEntry<Int, ChecklistResponse>> {
        return checklistResponseMap.entries
    }

    fun updateOptionStateAtIndex(index: Int, state: Boolean) {
        _checklistOptions.update { current ->
            val currentChecklistItem = current[index]
            val updatedChecklistItem = currentChecklistItem.copy(isChecked = state)

            val mutableList = current.toMutableList()
            mutableList[index] = updatedChecklistItem

            mutableList.toList()
        }
    }

    // Store response to questions

    // Commit responses to database - database to be added in later feature branch

    companion object {
        val CSV_REPOSITORY_KEY = object : CreationExtras.Key<CsvRepositoryImpl> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val csvRepository = this[CSV_REPOSITORY_KEY] as CsvRepositoryImpl
                BaselineViewModel(csvRepository)
            }
        }
    }
}