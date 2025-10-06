package com.ichinweze.flickpick.interfaces

interface CsvRepository {
    suspend fun getCsvLines(fileName: String, includeHeader: Boolean): List<String>
}