package com.ichinweze.flickpick.viewmodels.utils

import com.ichinweze.flickpick.data.ViewModelData.GenreData
import com.ichinweze.flickpick.data.ViewModelData.MovieRegionData

object ViewModelUtils {

    data class ChecklistItem(
        val index: Int,
        val checklistItem: String,
        var isChecked: Boolean = false
    )

    data class ChecklistResponse(val responses: List<Int>)

    fun convertGenreToChecklistItem(genreData: GenreData): ChecklistItem {
        return ChecklistItem(
            index = genreData.index,
            checklistItem = genreData.genre
        )
    }

    fun makeMovieRegionOption(movieRegionData: MovieRegionData): String {
        val bracketedSuffix =
            if (movieRegionData.industryName != "") "(${movieRegionData.industryName})"
            else ""

        return "${movieRegionData.country} $bracketedSuffix".trim()
    }

    fun convertMovieRegionToChecklistItem(movieRegionData: MovieRegionData): ChecklistItem {
        return ChecklistItem(
            index = movieRegionData.index,
            checklistItem = makeMovieRegionOption(movieRegionData)
        )
    }

    fun makeIntervalFromLbAndUb(lb: String, ub: String): String {
        val left = if (lb == "_") "<" else lb
        val connector =
            if (lb == "_") " "
            else if (ub == "_") ">"
            else "-"
        val right = if (ub == "_") "" else ub

        return left + connector + right
    }
}