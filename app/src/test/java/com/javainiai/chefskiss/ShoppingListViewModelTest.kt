package com.javainiai.chefskiss

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingService
import com.javainiai.chefskiss.data.database.shoppinglist.ShopIngredient
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListUiState
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShoppingListViewModelTest {
    private lateinit var shoppingService: ShoppingService
    private lateinit var recipeService: RecipeService
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        shoppingService = mockk(relaxed = true)
        recipeService = mockk(relaxed = true)
        viewModel = ShoppingListViewModel(context, shoppingService, recipeService)
    }

    @Test
    fun `test uiState`() = runBlocking {
        val expected = ShoppingListUiState(listOf())
        coEvery { shoppingService.getShoppingList() } returns flowOf(listOf())
        coEvery { recipeService.getRecipesWithIngredients(any()) } returns flowOf(listOf())
        assertEquals(expected, viewModel.uiState.value)
    }

    @Test
    fun `test checkedIngredients`() = runBlocking {
        val expected = listOf<ShopIngredient>()
        coEvery { shoppingService.getShoppingCheckedIngredients() } returns flowOf(listOf())
        assertEquals(expected, viewModel.checkedIngredients.value)
    }

    @Test
    fun `test removeRecipe`() = runBlocking {
        val recipe = Recipe(
            title = "Test Recipe",
            description = "",
            cookingTime = 0,
            servings = 0,
            rating = 0,
            favorite = false,
            imagePath = Uri.EMPTY
        )
        viewModel.removeRecipe(recipe)
        coVerify { shoppingService.deleteShopRecipe(ShopRecipe(recipe.id)) }
    }

    @Test
    fun `test addCheckedIngredient`() = runBlocking {
        val ingredient = ShopIngredient(ingredientId = 2, recipeId = 1)
        viewModel.addCheckedIngredient(ingredient)
        coVerify { shoppingService.insertShopIngredient(ingredient) }
    }

    @Test
    fun `test removeCheckedIngredient`() = runBlocking {
        val ingredient = ShopIngredient(ingredientId = 2, recipeId = 1)
        viewModel.removeCheckedIngredient(ingredient)
        coVerify { shoppingService.deleteShopIngredient(ingredient) }
    }
}