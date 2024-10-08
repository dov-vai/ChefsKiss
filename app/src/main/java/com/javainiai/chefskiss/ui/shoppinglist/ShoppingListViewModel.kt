package com.javainiai.chefskiss.ui.shoppinglist

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingService
import com.javainiai.chefskiss.data.database.shoppinglist.ShopIngredient
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import com.javainiai.chefskiss.ui.components.viewmodel.BaseViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


data class ShoppingListUiState(
    val recipesWithIngredients: List<RecipeWithIngredients>
)

class ShoppingListViewModel(
    private val context: Context,
    private val shoppingService: ShoppingService,
    private val recipeService: RecipeService,
) : BaseViewModel() {

    val uiState: StateFlow<ShoppingListUiState> =
        shoppingService
            .getShoppingList()
            .map { shoppingList ->
                ShoppingListUiState(
                    recipeService.getRecipesWithIngredients(shoppingList.map { it.recipeId })
                        .first()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = ShoppingListUiState(listOf())
            )

    val checkedIngredients: StateFlow<List<ShopIngredient>> =
        shoppingService
            .getShoppingCheckedIngredients()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = listOf()
            )

    fun removeRecipe(recipe: Recipe) {
        viewModelScope.launch {
            shoppingService.deleteShopRecipe(ShopRecipe(recipe.id))
        }
        showMessage(context.getString(R.string.removed, recipe.title))
    }

    fun addCheckedIngredient(ingredient: ShopIngredient) {
        viewModelScope.launch {
            shoppingService.insertShopIngredient(ingredient)
        }
    }

    fun removeCheckedIngredient(ingredient: ShopIngredient) {
        viewModelScope.launch {
            shoppingService.deleteShopIngredient(ingredient)
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}