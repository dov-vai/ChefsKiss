package com.javainiai.chefskiss

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.database.recipe.RecipeWithTags
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerService
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingService
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.enums.CookingUnit
import com.javainiai.chefskiss.data.enums.Meal
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
    private val recipeService = mockk<RecipeService>(relaxed = true)
    private val shoppingService = mockk<ShoppingService>(relaxed = true)
    private val plannerService = mockk<PlannerService>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>()
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        coEvery { savedStateHandle.get<Long>(any()) } returns 1L
        viewModel = RecipeDetailsViewModel(
            context,
            savedStateHandle,
            recipeService,
            shoppingService,
            plannerService
        )
    }

    @Test
    fun `test uiState`() = runBlocking {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)
        val ingredients = listOf(
            Ingredient(
                recipeId = 1,
                name = "Test Ingredient",
                size = 1f,
                unit = CookingUnit.Gram
            )
        )
        val recipeWithIngredients = RecipeWithIngredients(recipe, ingredients)

        coEvery { recipeService.getRecipeWithIngredients(any()) } returns flowOf(
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

        coEvery { recipeService.getRecipeWithTags(any()) } returns flowOf(recipeWithTags)

        assertEquals(tags, viewModel.tags.value)
    }

    @Test
    fun `test deleteRecipe`() = runBlocking {
        viewModel.deleteRecipe()

        coVerify { recipeService.deleteRecipe(any()) }
    }

    @Test
    fun `test addToShoppingList`() = runBlocking {
        viewModel.addToShoppingList()

        coVerify { shoppingService.insertShopRecipe(any()) }
    }

    @Test
    fun `test updateFavorite`() = runBlocking {
        viewModel.updateFavorite()

        coVerify { recipeService.updateRecipe(any()) }
    }

    @Test
    fun `test updateRating`() = runBlocking {
        viewModel.updateRating(5)

        coVerify { recipeService.updateRecipe(any()) }
    }

    @Test
    fun `test updateCheckedIngredients`() = runBlocking {
        val ingredients = listOf(
            Ingredient(
                recipeId = 1,
                name = "Test Ingredient",
                size = 1f,
                unit = CookingUnit.Gram
            )
        )

        viewModel.updateCheckedIngredients(ingredients)

        assertEquals(ingredients, viewModel.checkedIngredients.value)
    }

    @Test
    fun `test addToMealPlanner`() = runBlocking {
        val plannerRecipe = PlannerRecipe(recipeId = 1, date = "2024-04-20", type = Meal.BREAKFAST)

        coEvery { plannerService.insertPlannerRecipe(any()) } returns Unit

        viewModel.addToMealPlanner(plannerRecipe)

        coVerify { plannerService.insertPlannerRecipe(plannerRecipe) }
    }

}