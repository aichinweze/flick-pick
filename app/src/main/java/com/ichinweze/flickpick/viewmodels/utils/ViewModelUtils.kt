package com.ichinweze.flickpick.viewmodels.utils

object ViewModelUtils {

    data class ChecklistItem(
        val index: Int,
        val checklistItem: String,
        var isChecked: Boolean = false
    )

    data class ChecklistResponse(val responses: List<Int>)

}