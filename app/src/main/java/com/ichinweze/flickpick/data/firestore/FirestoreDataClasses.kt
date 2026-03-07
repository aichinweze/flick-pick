package com.ichinweze.flickpick.data.firestore

data class UserAccountDetails(
    val name: String? = null,
    val age: String? = null
)

data class BaselineQuestions(
    val questionIndex: Int,
    val question: String,
    val responses: List<String>
)