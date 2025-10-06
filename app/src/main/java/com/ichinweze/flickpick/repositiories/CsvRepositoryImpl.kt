package com.ichinweze.flickpick.repositiories

import android.content.Context
import com.ichinweze.flickpick.interfaces.CsvRepository
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
}