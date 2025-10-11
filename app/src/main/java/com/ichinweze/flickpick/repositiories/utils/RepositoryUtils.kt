package com.ichinweze.flickpick.repositiories.utils

import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieQualityData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData
import com.ichinweze.flickpick.data.ViewModelData.QuestionData
import com.ichinweze.flickpick.data.ViewModelData.TimeBoundData
import kotlin.collections.first
import kotlin.text.split
import kotlin.text.toInt

object RepositoryUtils {

    const val BASELINE_QUESTIONS_CSV = "baseline/baseline-questions.csv"
    const val GENRE_LIST_CSV = "genre-list.csv"
    const val MOVIE_REGION_CSV = "movie-region.csv"
    const val RECOMMEND_QUESTIONS_CSV = "recommend/recommend-questions.csv"
    const val MOVIE_QUALITY_CSV = "recommend/movie-quality.csv"
    const val MOVIE_RUNTIME_CSV = "recommend/movie-runtime.csv"
    const val RELEASE_DECADE_CSV = "recommend/release-decade.csv"

    const val OPTION_YES = "yes"

    const val NA = "na"

    fun mapRawLineToQuestionData(rawCsvLine: String): QuestionData {
        val splitLine = rawCsvLine.split(",").map { it -> it.trim() }

        val index = splitLine.first().toInt()
        val question = splitLine[1]
        val isOptional = splitLine.last() == OPTION_YES

        return QuestionData(index = index, question = question, isOptional = isOptional)
    }

    fun mapRawLineToGenreData(rawCsvLine: String): GenreData {
        val splitLine = rawCsvLine.split(",").map { it -> it.trim() }
        
        val index = splitLine.first().toInt()
        val genre = splitLine.last()

        return GenreData(index = index, genre = genre)
    }

    fun mapRawLineToMovieRegionData(rawCsvLine: String): MovieRegionData {
        val splitLine = rawCsvLine.split(",").map { it -> it.trim() }
        
        val index = splitLine.first().toInt()
        val cleanedIndustryName = splitLine[1]

        val industryName =
            if (cleanedIndustryName == NA) ""
            else cleanedIndustryName
        val country = splitLine.last()

        return MovieRegionData(index = index, industryName = industryName, country = country)
    }

    fun mapRawLineToMovieQuality(rawCsvLine: String): MovieQualityData {
        val splitLine = rawCsvLine.split(",").map { it -> it.trim() }

        val index = splitLine.first().toInt()
        val quality = splitLine.last()

        return MovieQualityData(index = index, quality = quality)
    }

    fun mapRawLineToTimeBoundData(rawCsvLine: String): TimeBoundData {
        val splitLine = rawCsvLine.split(",").map { it -> it.trim() }

        val index = splitLine.first().toInt()
        val timeBoundLb = splitLine[1]
        val timeBoundUb = splitLine.last()

        return TimeBoundData(index = index, timeBoundLb = timeBoundLb, timeBoundUb = timeBoundUb)
    }
}