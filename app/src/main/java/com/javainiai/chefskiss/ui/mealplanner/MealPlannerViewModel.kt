package com.javainiai.chefskiss.ui.mealplanner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.CalendarUtils.getDateString
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
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
            "",
            CalendarUtils.getCurrentDate(),
            CalendarUtils.getStartOfWeek()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        updateTitle()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val plannerRecipes: StateFlow<Map<String, List<PlannerRecipeWithRecipe>>> =
        _uiState.flatMapLatest { mealPlannerState ->
            recipesRepository
                .getPlannerRecipesWithRecipes(
                    (0..6).map {
                        val date = CalendarUtils.datePlusOffset(mealPlannerState.startOfWeek, it)
                        date.getDateString()
                    }
                )
        }
            .map {
                withContext(Dispatchers.IO) {
                    it.groupBy { it.plannerRecipe.date }
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = mapOf()
            )

    private fun titleFormat(): String {
        val start = _uiState.value.startOfWeek
        val end = CalendarUtils.datePlusOffset(start, 6)
        val current = CalendarUtils.getCurrentDate()

        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val currentFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

        return "${dayFormat.format(start.time)}-${dayFormat.format(end.time)}, ${
            currentFormat.format(
                current.time
            )
        }"
    }

    private fun updateTitle() {
        _uiState.update { currentState ->
            currentState.copy(
                title = titleFormat()
            )
        }
    }

    fun shiftForward() {
        _uiState.update { currentState ->
            currentState.copy(
                startOfWeek = CalendarUtils.datePlusOffset(currentState.startOfWeek, 7)
            )
        }
        updateTitle()
    }

    fun shiftBackwards() {
        _uiState.update { currentState ->
            currentState.copy(
                startOfWeek = CalendarUtils.datePlusOffset(currentState.startOfWeek, -7)
            )
        }
        updateTitle()
    }

    fun revertStartOfWeek() {
        _uiState.update { currentState ->
            currentState.copy(
                startOfWeek = CalendarUtils.getStartOfWeek()
            )
        }
        updateTitle()
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}