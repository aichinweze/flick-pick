package com.ichinweze.flickpick.repositories

import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieQualityData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToQuestionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToGenreData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRegionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieQuality
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToMovieRuntime
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawLineToReleaseDecade
import org.junit.Test

class RepositoryUtilsTest {

    @Test
    fun testMapRawLineToQuestionsData() {
        val questionOne = "1, Pick your top 3 favourite movie genres, no"
        val questionTwo = "2, How old are you? (optional), yes"

        val questionLines = listOf<String>(questionOne, questionTwo)

        val baselineQuestions = questionLines.map { questionLine ->
            mapRawLineToQuestionData(questionLine)
        }

        val expectedBaselineQOne = QuestionData(
            index = 1,
            question = "Pick your top 3 favourite movie genres",
            isOptional = false
        )
        val expectedBaselineQTwo = QuestionData(
            index = 2,
            question = "How old are you? (optional)",
            isOptional = true
        )

        assert(baselineQuestions.size == 2) { "Unexpected length of mapped questions" }
        assert(baselineQuestions.first() == expectedBaselineQOne) { "First element does not match expected value" }
        assert(baselineQuestions.last() == expectedBaselineQTwo)  { "Second element does not match expected value" }
    }

    @Test
    fun testMapRawLineToGenreData() {

        val genre1 = "0,Action"
        val genre2 = "1,Adventure"
        val genre3 = "2,Comedy"

        val rawGenreList = listOf<String>(genre1, genre2, genre3)
        val genreList = rawGenreList.map { mapRawLineToGenreData(it) }

        val expectedGenre1 = GenreData(index = 0, genre = "Action")
        val expectedGenre2 = GenreData(index = 1, genre = "Adventure")
        val expectedGenre3 = GenreData(index = 2, genre = "Comedy")

        assert(genreList.size == 3) { "Unexpected number of genres" }
        assert(genreList.first() == expectedGenre1) { "First element does not match expected values" }
        assert(genreList.last() == expectedGenre3) { "Final element does not match expected values" }
    }

    @Test
    fun testMapRawLineToMovieRegionData() {
        val region1 = "0,na,UK"
        val region2 = "1,Hollywood,USA"

        val rawRegions = listOf<String>(region1, region2)
        val regionList = rawRegions.map { mapRawLineToMovieRegionData(it) }

        assert(regionList.size == 2) { "Unexpected number of regions" }
        assert(regionList.first().industryName == "") { "na industry not properly cleaned" }
        assert(regionList.last().country == "USA")
    }

    @Test
    fun testMapRawLineToMovieQuality() {
        val movieQuality = "0,Trashy"

        val expectedMovieQuality = MovieQualityData(index = 0, quality = "Trashy")
        val generatedMovieQuality = mapRawLineToMovieQuality(movieQuality)

        assert(generatedMovieQuality == expectedMovieQuality) { "Mapping not completed as expected" }
    }

    @Test
    fun testMapRawLineToMovieRuntime() {
        val runtime1 = "0,_,60"
        val runtime2 = "1,60,75"
        val runtime3 = "2,75,_"

        val rawRuntimes = listOf<String>(runtime1, runtime2, runtime3)
        val runtimeList = rawRuntimes.map { mapRawLineToMovieRuntime(it) }

        assert(runtimeList.size == 3) { "Unexpected number of runtimes" }
        assert(runtimeList.first().runtimeLb == "_")
        assert(runtimeList.first().runtimeUb == "60")
        assert(runtimeList.last().runtimeUb == "_")
    }

    @Test
    fun testMapRawLineToReleaseDecade() {
        val decade1 = "0,_,1950"
        val decade2 = "1,1950,1960"
        val decade3 = "2,2020,_"

        val rawDecades = listOf<String>(decade1, decade2, decade3)
        val decadeList = rawDecades.map { mapRawLineToReleaseDecade(it) }

        assert(decadeList.size == 3) { "Unexpected number of runtimes" }
        assert(decadeList.first().decadeLb == "_")
        assert(decadeList.first().decadeUb == "1950")
        assert(decadeList.last().decadeUb == "_")
    }
}