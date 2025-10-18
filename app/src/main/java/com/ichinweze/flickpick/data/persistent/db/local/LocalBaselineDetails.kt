package com.ichinweze.flickpick.data.persistent.db.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ichinweze.flickpick.data.ScreenData.BaselineDetails

@Entity(tableName = "baseline-details")
data class LocalBaselineDetails(
    @PrimaryKey val email: String,
    val baselineQuestionIndex: Int,
    val baselineResponses: String
)

fun LocalBaselineDetails.toExternal() = BaselineDetails(
    baselineQuestionIndex = baselineQuestionIndex,
    baselineResponses = baselineResponses.split("--").map { id -> id.toInt() }
)

fun BaselineDetails.toLocal(email: String) = LocalBaselineDetails(
    email = email,
    baselineQuestionIndex = baselineQuestionIndex,
    baselineResponses = baselineResponses.joinToString(separator = "--")
)