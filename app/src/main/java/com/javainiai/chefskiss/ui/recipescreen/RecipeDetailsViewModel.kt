package com.javainiai.chefskiss.ui.recipescreen

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerService
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingService
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.enums.UnitSystem
import com.javainiai.chefskiss.ui.components.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecipeDisplayUiState(
    val recipe: Recipe,
    val ingredients: List<Ingredient>,
)

class RecipeDetailsViewModel(
    private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val recipeService: RecipeService,
    private val shoppingService: ShoppingService,
    private val plannerService: PlannerService
) : BaseViewModel() {
    private val recipeId: Long =
        checkNotNull(savedStateHandle[RecipeDetailsDestination.recipeIdArg])

    val uiState: StateFlow<RecipeDisplayUiState> = recipeService
        .getRecipeWithIngredients(recipeId)
        .filterNotNull()
        .map {
            adjustServingSize(it.recipe.servings)
            RecipeDisplayUiState(recipe = it.recipe, ingredients = it.ingredients)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = RecipeDisplayUiState(
                Recipe(0, "", "", 0, 0, 0, false, Uri.EMPTY), listOf()
            )
        )

    val tags: StateFlow<List<Tag>> = recipeService
        .getRecipeWithTags(recipeId)
        .filterNotNull()
        .map { it.tags }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    private val _customServingSize = MutableStateFlow(uiState.value.recipe.servings)
    val customServingSize = _customServingSize.asStateFlow()

    private var _checkedIngredients = MutableStateFlow(listOf<Ingredient>())
    val checkedIngredients = _checkedIngredients.asStateFlow()

    private var _unitSystem = MutableStateFlow(UnitSystem.Metric)
    val unitSystem = _unitSystem.asStateFlow()

    var screenIndex by mutableIntStateOf(0)
        private set

    fun updateScreenIndex(index: Int) {
        screenIndex = index
    }

    suspend fun deleteRecipe() {
        recipeService.deleteRecipe(uiState.value.recipe)
    }

    fun addToShoppingList() {
        viewModelScope.launch {
            shoppingService.insertShopRecipe(ShopRecipe(uiState.value.recipe.id))
        }
        showMessage(context.getString(R.string.added_to_shopping_list))
    }

    fun updateFavorite() {
        val updatedRecipe = uiState.value.recipe.copy(favorite = !uiState.value.recipe.favorite)
        viewModelScope.launch {
            recipeService.updateRecipe(updatedRecipe)
        }
        showMessage(
            if (!uiState.value.recipe.favorite) context.getString(R.string.added_to_favorites) else context.getString(
                R.string.removed_from_favorites
            )
        )
    }

    fun updateRating(rating: Int) {
        val updatedRecipe = uiState.value.recipe.copy(rating = rating)
        viewModelScope.launch {
            recipeService.updateRecipe(updatedRecipe)
        }
    }

    fun updateCheckedIngredients(ingredients: List<Ingredient>) {
        _checkedIngredients.update { ingredients }
    }

    fun addToMealPlanner(plannerRecipe: PlannerRecipe) {
        viewModelScope.launch {
            plannerService.insertPlannerRecipe(plannerRecipe)
        }
        showMessage(
            context.getString(
                R.string.added_to_as,
                plannerRecipe.date,
                plannerRecipe.type.getTitle(context)
            )
        )
    }

    fun adjustServingSize(size: Int) {
        if (size >= 1) {
            _customServingSize.update { size }
        } else showMessage(context.getString(R.string.serving_size_can_t_be_lower_than_1))
    }

    fun updateUnitSystem(system: UnitSystem) {
        _unitSystem.update { system }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
