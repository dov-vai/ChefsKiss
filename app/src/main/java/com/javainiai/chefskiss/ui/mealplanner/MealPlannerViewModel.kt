package com.javainiai.chefskiss.ui.mealplanner

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import com.javainiai.chefskiss.data.utils.CalendarUtils
import com.javainiai.chefskiss.data.utils.CalendarUtils.getDate
import com.javainiai.chefskiss.data.utils.CalendarUtils.getDateString
import com.javainiai.chefskiss.ui.components.viewmodel.BaseViewModel
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealPlannerUiState(
    val title: String,
    val currentDate: Date,
    val startOfWeek: Date,
    val bulkEditMode: Boolean,
    val bulkEditWeek: Date,
    val selectedRecipes: List<PlannerRecipeWithRecipe>
)

class MealPlannerViewModel(
    private val context: Context,
    private val recipesRepository: RecipesRepository
) : BaseViewModel() {
    private var _uiState = MutableStateFlow(
        MealPlannerUiState(
            "",
            CalendarUtils.getCurrentDate(),
            CalendarUtils.getStartOfWeek(),
            false,
            CalendarUtils.getStartOfWeek(),
            listOf()
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
        val dayFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
        return "${dayFormat.format(start.time)} - ${dayFormat.format(end.time)}"
    }

    private fun updateTitle() {
        _uiState.update { currentState ->
            currentState.copy(
                title = titleFormat()
            )
        }
    }

    fun shiftForward() {
        updateStartOfWeek(CalendarUtils.datePlusOffset(_uiState.value.startOfWeek, 7))
    }

    fun shiftBackwards() {
        updateStartOfWeek(CalendarUtils.datePlusOffset(_uiState.value.startOfWeek, -7))
    }

    fun revertStartOfWeek() {
        updateStartOfWeek(CalendarUtils.getStartOfWeek())
    }

    fun updateStartOfWeek(date: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                startOfWeek = date
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
        showMessage(context.getString(R.string.added_to_shopping_list))
    }

    fun updateBulkEditMode(bulkEditMode: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                bulkEditMode = bulkEditMode
            )
        }
    }

    fun updateBulkEditWeek(bulkEditWeek: Date) {
        _uiState.update { currentState ->
            currentState.copy(
                bulkEditWeek = bulkEditWeek
            )
        }
    }

    fun updateSelectedRecipes(recipes: List<PlannerRecipeWithRecipe>) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedRecipes = recipes
            )
        }
    }

    private fun editMeals(startingDate: Date, isCopyOperation: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedPlannerRecipes = mutableListOf<PlannerRecipeWithRecipe>()
            val groupedRecipes = _uiState.value.selectedRecipes
                .sortedBy { it.plannerRecipe.date }
                .groupBy { it.plannerRecipe.date }

            groupedRecipes.keys.firstOrNull()?.getDate()?.let { firstDate ->
                groupedRecipes.keys.forEach { date ->
                    val offset = CalendarUtils.getDaysDifference(firstDate, date.getDate()!!)
                    val dateToInsert =
                        CalendarUtils.datePlusOffset(startingDate, offset.toInt()).getDateString()
                    groupedRecipes[date]?.forEach {
                        val plannerRecipe = it.plannerRecipe.copy(
                            id = if (isCopyOperation) 0 else it.plannerRecipe.id,
                            date = dateToInsert
                        )

                        if (isCopyOperation)
                            recipesRepository.insertPlannerRecipe(plannerRecipe)
                        else {
                            recipesRepository.updatePlannerRecipe(plannerRecipe)
                            updatedPlannerRecipes.add(it.copy(plannerRecipe = plannerRecipe))
                        }
                    }
                }
            }

            if (!isCopyOperation) {
                updateSelectedRecipes(updatedPlannerRecipes)
            }
        }
    }

    fun copyMeals(startingDate: Date) {
        editMeals(startingDate, true)
    }

    fun moveMeals(startingDate: Date) {
        editMeals(startingDate, false)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}