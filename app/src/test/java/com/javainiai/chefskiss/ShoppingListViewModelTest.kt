package com.javainiai.chefskiss

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopIngredient
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListUiState
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShoppingListViewModelTest {
    private lateinit var recipesRepository: RecipesRepository
    private lateinit var viewModel: ShoppingListViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        recipesRepository = mockk(relaxed = true)
        viewModel = ShoppingListViewModel(context, recipesRepository)
    }

    @Test
    fun `test uiState`() = runBlocking {
        val expected = ShoppingListUiState(listOf())
        coEvery { recipesRepository.getShoppingList() } returns flowOf(listOf())
        coEvery { recipesRepository.getRecipesWithIngredients(any()) } returns flowOf(listOf())
        assertEquals(expected, viewModel.uiState.value)
    }

    @Test
    fun `test checkedIngredients`() = runBlocking {
        val expected = listOf<ShopIngredient>()
        coEvery { recipesRepository.getShoppingCheckedIngredients() } returns flowOf(listOf())
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
        coVerify { recipesRepository.deleteShopRecipe(ShopRecipe(recipe.id)) }
    }

    @Test
    fun `test addCheckedIngredient`() = runBlocking {
        val ingredient = ShopIngredient(ingredientId = 2, recipeId = 1)
        viewModel.addCheckedIngredient(ingredient)
        coVerify { recipesRepository.insertShopIngredient(ingredient) }
    }

    @Test
    fun `test removeCheckedIngredient`() = runBlocking {
        val ingredient = ShopIngredient(ingredientId = 2, recipeId = 1)
        viewModel.removeCheckedIngredient(ingredient)
        coVerify { recipesRepository.deleteShopIngredient(ingredient) }
    }
}