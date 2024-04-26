package com.javainiai.chefskiss.ui.shoppinglist

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopIngredient
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class ShoppingListUiState(
    val recipesWithIngredients: List<RecipeWithIngredients>
)

class ShoppingListViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {

    val uiState: StateFlow<ShoppingListUiState> =
        recipesRepository
            .getShoppingList()
            .map { shoppingList ->
                ShoppingListUiState(
                    recipesRepository.getRecipesWithIngredients(shoppingList.map { it.recipeId })
                        .first()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ShoppingListUiState(listOf())
            )

    val checkedIngredients: StateFlow<List<ShopIngredient>> =
        recipesRepository
            .getShoppingCheckedIngredients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    val snackbarHostState = SnackbarHostState()

    var messageInProgress: Job? = null
        private set

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

    fun removeRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipesRepository.deleteShopRecipe(ShopRecipe(recipe.id))
        }
        showMessage("Removed ${recipe.title}")
    }

    fun addCheckedIngredient(ingredient: ShopIngredient) {
        viewModelScope.launch {
            recipesRepository.insertShopIngredient(ingredient)
        }
    }

    fun removeCheckedIngredient(ingredient: ShopIngredient) {
        viewModelScope.launch {
            recipesRepository.deleteShopIngredient(ingredient)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}