package com.ichinweze.flickpick.repositiories.utils

import com.ichinweze.flickpick.data.ViewModelData.BaselineQuestionData
import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData
import kotlin.collections.first
import kotlin.text.toInt

object RepositoryUtils {

    const val BASELINE_QUESTIONS_CSV = "baseline-questions.csv"
    const val GENRE_LIST_CSV = "genre-list.csv"
    const val MOVIE_REGION_CSV = "movie-region.csv"

    const val OPTION_YES = "yes"

    const val NA = "na"

    fun mapRawToBaselineQuestion(rawCsvLine: List<String>): BaselineQuestionData {
        val index = rawCsvLine.first().trim().toInt()
        val question = rawCsvLine[1].trim()
        val isOptional = rawCsvLine.last() == OPTION_YES

        return BaselineQuestionData(index = index, question = question, isOptional = isOptional)
    }

    fun mapRawToGenre(rawCsvLine: List<String>): GenreData {
        val index = rawCsvLine.first().trim().toInt()
        val genre = rawCsvLine[1].trim()

        return GenreData(index = index, genre = genre)
    }

    fun mapRawToMovieRegion(rawCsvLine: List<String>): MovieRegionData {
        val index = rawCsvLine.first().trim().toInt()
        val cleanedIndustryName = rawCsvLine[1].trim()

        val industryName =
            if (cleanedIndustryName == NA) ""
            else cleanedIndustryName
        val country = rawCsvLine.last().trim()

        return MovieRegionData(index = index, industryName = industryName, country = country)
    }
}