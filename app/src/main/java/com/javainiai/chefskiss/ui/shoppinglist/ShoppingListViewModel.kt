package com.javainiai.chefskiss.ui.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopRecipe
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
        recipesRepository.getShoppingList().map { shoppingList ->
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


    fun removeRecipe(recipe: Recipe) {
        viewModelScope.launch {
            recipesRepository.deleteShopRecipe(ShopRecipe(recipe.id))
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}