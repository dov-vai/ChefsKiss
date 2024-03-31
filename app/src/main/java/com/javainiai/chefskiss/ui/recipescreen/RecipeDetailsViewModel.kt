package com.javainiai.chefskiss.ui.recipescreen

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.Job
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
    savedStateHandle: SavedStateHandle,
    private val recipesRepository: RecipesRepository
) : ViewModel() {
    private val recipeId: Long =
        checkNotNull(savedStateHandle[RecipeDetailsDestination.recipeIdArg])

    val uiState: StateFlow<RecipeDisplayUiState> = recipesRepository
        .getRecipeWithIngredients(recipeId)
        .filterNotNull()
        .map { RecipeDisplayUiState(recipe = it.recipe, ingredients = it.ingredients) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = RecipeDisplayUiState(
                Recipe(0, "", "", 0, 0, 0, false, Uri.EMPTY), listOf()
            )
        )

    val tags: StateFlow<List<Tag>> = recipesRepository
        .getRecipeWithTags(recipeId)
        .filterNotNull()
        .map { it.tags }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    private var _checkedIngredients = MutableStateFlow(listOf<Ingredient>())
    val checkedIngredients = _checkedIngredients.asStateFlow()

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

    var screenIndex by mutableIntStateOf(0)
        private set

    fun updateScreenIndex(index: Int) {
        screenIndex = index
    }

    suspend fun deleteRecipe() {
        recipesRepository.deleteRecipe(uiState.value.recipe)
    }

    fun addToShoppingList() {
        viewModelScope.launch {
            recipesRepository.insertShopRecipe(ShopRecipe(uiState.value.recipe.id))
        }
        showMessage("Added to shopping list")
    }

    fun updateFavorite() {
        val updatedRecipe = uiState.value.recipe.copy(favorite = !uiState.value.recipe.favorite)
        viewModelScope.launch {
            recipesRepository.updateRecipe(updatedRecipe)
        }
        showMessage(if (!uiState.value.recipe.favorite) "Added to favorites" else "Removed from favorites")
    }

    fun updateRating(rating: Int) {
        val updatedRecipe = uiState.value.recipe.copy(rating = rating)
        viewModelScope.launch {
            recipesRepository.updateRecipe(updatedRecipe)
        }
    }

    fun updateCheckedIngredients(ingredients: List<Ingredient>) {
        _checkedIngredients.update { ingredients }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}
