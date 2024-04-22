package com.javainiai.chefskiss.ui.mealplanner

import androidx.annotation.VisibleForTesting
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.CalendarUtils.getDateString
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealPlannerUiState(
    val title: String,
    val currentDate: Date,
    val startOfWeek: Date
)

class MealPlannerViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private var _uiState = MutableStateFlow(
        MealPlannerUiState(
            "",
            CalendarUtils.getCurrentDate(),
            CalendarUtils.getStartOfWeek()
        )
    )

    val snackbarHostState = SnackbarHostState()

    private var messageInProgress: Job? = null
    private fun showMessage(message: String) {
        // cancel in case it hasn't finished so the message can be shown immediately
        messageInProgress?.cancel()
        messageInProgress = viewModelScope.launch {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
        }
    }

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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public fun titleFormat(): String {
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

    fun addToShoppingList(plannerRecipeWithRecipes: List<PlannerRecipeWithRecipe>?) {
        plannerRecipeWithRecipes?.forEach { recipe ->
            viewModelScope.launch(Dispatchers.IO) {
                recipesRepository.insertShopRecipe(ShopRecipe(recipe.recipe.id))
            }
        }
        showMessage("Added to shopping list")
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}