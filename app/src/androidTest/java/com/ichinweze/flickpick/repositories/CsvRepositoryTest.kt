package com.ichinweze.flickpick.repositories

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ichinweze.flickpick.repositiories.CsvRepositoryImpl
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.BASELINE_QUESTIONS_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.GENRE_LIST_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.MOVIE_REGION_CSV
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToGenreData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRegionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToQuestionData
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CsvRepositoryTest {

    val context = InstrumentationRegistry.getInstrumentation().context
    val csvRepository = CsvRepositoryImpl(context)

    // TODO: Add some cases of bad CSV data for each??

    @Test
    fun testReadBaselineQuestionsFromAssets() = runTest {
        val fileName = "baseline/baseline-questions-clean.csv"

        val baselineQuestionsData = csvRepository
            .getCsvLines(fileName, false)
            .map { mapRawLineToQuestionData(it) }

        val firstBaselineQuestion = baselineQuestionsData.first()
        val secondBaselineQuestion = baselineQuestionsData.last()

        assert(baselineQuestionsData.isNotEmpty()) { "CSV file is empty" }
        assert(baselineQuestionsData.size == 2) { "Unexpected count of baseline questions" }
        assert(firstBaselineQuestion.index == 0) { "Unexpected indexing of baseline questions" }
        assert(firstBaselineQuestion.question == "Pick your top 3 favourite movie genres") {
            "Unexpected question presented by first row of CSV"
        }
        assert(firstBaselineQuestion.isOptional == false) { "Baseline Question should not be optional" }
        assert(secondBaselineQuestion.index == 1) { "Unexpected indexing of baseline questions" }
    }

    @Test
    fun testReadGenreListFromAssets() = runTest {
        val fileName = "genre-list-clean.csv"

        val genreData = csvRepository
            .getCsvLines(fileName, false)
            .map { mapRawLineToGenreData(it) }

        val expectedGenres = listOf<String>("Action", "Adventure", "Comedy", "Drama", "Fantasy")

        assert(genreData.size == 5) { "Incorrect amount of genres read from CSV" }
        assert(genreData.filter{ genre -> genre.index == 1}.map{ it -> it.genre}.first() == "Adventure") {
            "Unexpected genre at specified index"
        }
        assert(genreData.map { data -> data.genre }.intersect(expectedGenres).size == 5) {
            "Unexpected number of movie genres are present"
        }
    }

    @Test
    fun testReadMovieRegionFromAssets() = runTest {
        val fileName = "movie-region-clean.csv"

        val movieRegionData = csvRepository
            .getCsvLines(fileName, false)
            .map{ mapRawLineToMovieRegionData(it) }

        val expectedCountries = listOf<String>("UK", "USA", "South Korea")
        val expectedIndustryNames = listOf<String>("Hollywood", "K-Drama")

        val ukMovieIndustryName = movieRegionData
            .filter { region -> region.country == "UK" }
            .map { region -> region.industryName }
            .distinct()
            .first()

        assert(movieRegionData.size == 3) { "Incorrect amount of movie regions read from CSV" }
        assert(ukMovieIndustryName == "") { "NA industry names cleaned out as expected" }
        assert(movieRegionData.map { data -> data.country }.intersect(expectedCountries).size == 3) {
            "Unexpected number of movie regions are present"
        }
        assert(movieRegionData.map { data -> data.industryName }.intersect(expectedIndustryNames).size == 2) {
            "Unexpected number of movie industry names are present"
        }
    }
}