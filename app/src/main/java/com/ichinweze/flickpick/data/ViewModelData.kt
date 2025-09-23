package com.ichinweze.flickpick.data

object ViewModelData {

    data class BaselineQuestionsData(
        val index: Int,
        val question: String,
        val isOptional: Boolean
    )

    const val SCREEN_UNINITIALISED = "screen_uninitialised"
    const val SCREEN_INITIALISING = "screen_initialising"
    const val SCREEN_INITIALISED = "screen_initialised"
}