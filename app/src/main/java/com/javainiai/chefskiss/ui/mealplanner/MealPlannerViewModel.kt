package com.javainiai.chefskiss.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    val plannerRecipes: StateFlow<Map<String, List<PlannerRecipeWithRecipe>>> =
        recipesRepository
            .getPlannerRecipesWithRecipes(
                (0..6).map {
                    val date = CalendarUtils.datePlusOffset(CalendarUtils.getStartOfWeek(), it)
                    dateFormat.format(date)
                }
            )
            .map {
                it.groupBy { it.plannerRecipe.date }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = mapOf()
            )

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

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}