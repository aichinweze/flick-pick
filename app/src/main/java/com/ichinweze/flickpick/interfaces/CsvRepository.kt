package com.ichinweze.flickpick.interfaces

import com.ichinweze.flickpick.data.ViewModelData.BaselineQuestionData
import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData

interface CsvRepository {
    suspend fun getCsvLines(fileName: String, includeHeader: Boolean): List<String>

    suspend fun getBaselineDataFromCsv(fileName: String): List<BaselineQuestionData>

    suspend fun getGenreDataFromCsv(fileName: String): List<GenreData>

    suspend fun getMovieRegionDataFromCsv(fileName: String): List<MovieRegionData>
}