package com.ichinweze.flickpick.repositories

import com.ichinweze.flickpick.data.ViewModelData.BaselineQuestionData
import com.ichinweze.flickpick.repositiories.utils.RepositoryUtils.mapRawToBaselineQuestion
import org.junit.Test

class RepositoryUtilsTest {

    @Test
    fun testMapRawCsvToBaselineQuestions() {
        val questionOne = listOf<String>("1", "Pick your top 3 favourite movie genres", "no")
        val questionTwo = listOf<String>("2", "How old are you? (optional)", "yes")

        val questionLines = listOf<List<String>>(questionOne, questionTwo)

        val baselineQuestions = questionLines.map { questionLine ->
            mapRawToBaselineQuestion(questionLine)
        }

        val expectedBaselineQOne = BaselineQuestionData(
            index = 1,
            question = "Pick your top 3 favourite movie genres",
            isOptional = false
        )
        val expectedBaselineQTwo = BaselineQuestionData(
            index = 2,
            question = "How old are you? (optional)",
            isOptional = true
        )

        assert(baselineQuestions.size == 2) { "Unexpected length of mapped questions" }
        assert(baselineQuestions.first() == expectedBaselineQOne) { "First element does not match expected value" }
        assert(baselineQuestions.last() == expectedBaselineQTwo)  { "Second element does not match expected value" }
    }
}