package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.data.ViewModelData.BaselineQuestionData
import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData
import com.ichinweze.flickpick.interfaces.CsvRepository
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawToBaselineQuestion
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawToGenre
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawToMovieRegion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class CsvRepositoryImpl(val context: Context) : CsvRepository {

    override suspend fun getCsvLines(fileName: String, includeHeader: Boolean): List<String> =
        withContext(Dispatchers.IO) {
            val data = mutableListOf<String>()
            try {
                context.assets.open(fileName).bufferedReader().useLines { lines ->
                    lines.forEach { line -> data.add(line) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (includeHeader) data
            else {
                data.removeAt(0)
                data
            }
        }

    override suspend fun getBaselineDataFromCsv(fileName: String): List<BaselineQuestionData> =
        withContext(Dispatchers.IO) {
            val rawCsvLines = mutableListOf<List<String>>()
            try {
                context.assets.open(fileName).bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val splitLine = line.split(",").map { it -> it.trim() }
                        rawCsvLines.add(splitLine)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            rawCsvLines.removeAt(0)
            rawCsvLines.map { rawCsvLine -> mapRawToBaselineQuestion(rawCsvLine) }
        }

    override suspend fun getGenreDataFromCsv(fileName: String): List<GenreData> =
        withContext(Dispatchers.IO) {
            val rawCsvLines = mutableListOf<String>()
            try {
                context.assets.open(fileName).bufferedReader().useLines { lines ->
                    lines.forEach { line -> rawCsvLines.add(line) }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            rawCsvLines.removeAt(0)
            rawCsvLines.map { rawCsvLine -> mapRawToGenre(rawCsvLine) }
        }

    override suspend fun getMovieRegionDataFromCsv(fileName: String): List<MovieRegionData> =
        withContext(Dispatchers.IO) {
            val rawCsvLines = mutableListOf<List<String>>()
            try {
                context.assets.open(fileName).bufferedReader().useLines { lines ->
                    lines.forEach { line ->
                        val splitLine = line.split(",").map { it -> it.trim() }
                        rawCsvLines.add(splitLine)
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            rawCsvLines.removeAt(0)
            rawCsvLines.map { rawCsvLine -> mapRawToMovieRegion(rawCsvLine) }
        }
}