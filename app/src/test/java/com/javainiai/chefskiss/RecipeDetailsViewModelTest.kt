package com.javainiai.chefskiss

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.javainiai.chefskiss.data.enums.Meal
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.PlannerRecipe
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.recipe.RecipeWithTags
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.recipe.ShopRecipe
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsViewModel
import com.javainiai.chefskiss.ui.recipescreen.RecipeDisplayUiState
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
class RecipeDetailsViewModelTest {
    private lateinit var viewModel: RecipeDetailsViewModel
    private val recipesRepository = mockk<RecipesRepository>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()

    @Before
    fun setup() {
        coEvery { savedStateHandle.get<Long>(any()) } returns 1L
        viewModel = RecipeDetailsViewModel(savedStateHandle, recipesRepository)
    }

    @Test
    fun `test uiState`() = runBlocking {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)
        val ingredients = listOf(
            Ingredient(
                recipeId = 1,
                name = "Test Ingredient",
                size = 1f,
                unit = "Test Unit"
            )
        )
        val recipeWithIngredients = RecipeWithIngredients(recipe, ingredients)

        coEvery { recipesRepository.getRecipeWithIngredients(any()) } returns flowOf(
            recipeWithIngredients
        )

        val expectedUiState = RecipeDisplayUiState(recipe, ingredients)
        assertEquals(expectedUiState, viewModel.uiState.value)
    }

    @Test
    fun `test tags`() = runBlocking {
        val tags = listOf(Tag(1, "Test Tag"))
        val recipeWithTags = RecipeWithTags(
            Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY),
            tags
        )

        coEvery { recipesRepository.getRecipeWithTags(any()) } returns flowOf(recipeWithTags)

        val expectedTags = tags
        assertEquals(expectedTags, viewModel.tags.value)
    }

    @Test
    fun `test deleteRecipe`() = runBlocking {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)

        coEvery { recipesRepository.deleteRecipe(any()) } returns Unit

        viewModel.deleteRecipe()

        coVerify { recipesRepository.deleteRecipe(recipe) }
    }

    @Test
    fun `test addToShoppingList`() = runBlocking {
        val shopRecipe = ShopRecipe(1)

        coEvery { recipesRepository.insertShopRecipe(any()) } returns Unit

        viewModel.addToShoppingList()

        coVerify { recipesRepository.insertShopRecipe(shopRecipe) }
    }

    @Test
    fun `test updateFavorite`() = runBlocking {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)
        val updatedRecipe = recipe.copy(favorite = !recipe.favorite)

        coEvery { recipesRepository.updateRecipe(any()) } returns Unit

        viewModel.updateFavorite()

        coVerify { recipesRepository.updateRecipe(updatedRecipe) }
    }

    @Test
    fun `test updateRating`() = runBlocking {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)
        val updatedRecipe = recipe.copy(rating = 5)

        coEvery { recipesRepository.updateRecipe(any()) } returns Unit

        viewModel.updateRating(5)

        coVerify { recipesRepository.updateRecipe(updatedRecipe) }
    }

    @Test
    fun `test updateCheckedIngredients`() = runBlocking {
        val ingredients = listOf(
            Ingredient(
                recipeId = 1,
                name = "Test Ingredient",
                size = 1f,
                unit = "Test Unit"
            )
        )

        viewModel.updateCheckedIngredients(ingredients)

        assertEquals(ingredients, viewModel.checkedIngredients.value)
    }

    @Test
    fun `test addToMealPlanner`() = runBlocking {
        val plannerRecipe = PlannerRecipe(recipeId = 1, date = "2024-04-20", type = Meal.BREAKFAST)

        coEvery { recipesRepository.insertPlannerRecipe(any()) } returns Unit

        viewModel.addToMealPlanner(plannerRecipe)

        coVerify { recipesRepository.insertPlannerRecipe(plannerRecipe) }
    }

}