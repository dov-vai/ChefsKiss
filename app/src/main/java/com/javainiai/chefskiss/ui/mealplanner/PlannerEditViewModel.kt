package com.javainiai.chefskiss.ui.mealplanner

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.planner.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerService
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource
import com.javainiai.chefskiss.data.enums.Meal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MealEditUiState(
    val title: String,
    val selectedType: Meal
)

class PlannerEditViewModel(
    selectedRecipeDataSource: SelectedRecipeDataSource,
    savedStateHandle: SavedStateHandle,
    private val recipeService: RecipeService,
    private val plannerService: PlannerService
) : ViewModel() {
    private val plannerDate: String =
        checkNotNull(savedStateHandle[PlannerEditDestination.plannerDateArg])

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedRecipe: StateFlow<Recipe?> = selectedRecipeDataSource.recipeId
        .filterNotNull()
        .flatMapLatest { recipeService.getRecipeStream(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = null
        )

    private var _uiState = MutableStateFlow(MealEditUiState(plannerDate, Meal.BREAKFAST))
    val uiState = _uiState.asStateFlow()

    val plannerRecipes: StateFlow<List<PlannerRecipeWithRecipe>> = plannerService
        .getPlannerRecipesWithRecipes(plannerDate)
        .filterNotNull()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    fun updateType(selectedType: Meal) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedType = selectedType
            )
        }
    }

    fun insertPlannerRecipe() {
        if (selectedRecipe.value == null)
            return

        with(_uiState.value) {
            viewModelScope.launch(Dispatchers.IO) {
                plannerService.insertPlannerRecipe(
                    PlannerRecipe(
                        date = plannerDate,
                        recipeId = selectedRecipe.value!!.id,
                        type = selectedType
                    )
                )
            }
        }
    }

    fun deletePlannerRecipe(recipe: PlannerRecipe) {
        viewModelScope.launch(Dispatchers.IO) {
            plannerService.deletePlannerRecipe(recipe)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}