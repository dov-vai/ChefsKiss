package com.javainiai.chefskiss.ui.mealplanner

import androidx.lifecycle.ViewModel
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealPlannerUiState(
    val title: String,
    val currentDate: Date,
    val startOfWeek: Date
)

class MealPlannerViewModel(recipesRepository: RecipesRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(
        MealPlannerUiState(
            titleFormat(),
            CalendarUtils.getCurrentDate(),
            CalendarUtils.getStartOfWeek()
        )
    )
    val uiState = _uiState.asStateFlow()

    private fun titleFormat(): String {
        val start = CalendarUtils.getStartOfWeek()
        val end = CalendarUtils.getEndOfWeek()
        val current = CalendarUtils.getCurrentDate()

        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val currentFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        return "${dayFormat.format(start.time)}-${dayFormat.format(end.time)}, ${
            currentFormat.format(
                current.time
            )
        }"
    }
}