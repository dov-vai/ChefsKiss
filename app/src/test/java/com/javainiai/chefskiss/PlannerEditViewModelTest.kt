package com.javainiai.chefskiss

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerService
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource
import com.javainiai.chefskiss.data.enums.Meal
import com.javainiai.chefskiss.ui.mealplanner.PlannerEditViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PlannerEditViewModelTest {
    private val recipeService = mockk<RecipeService>(relaxed = true)
    private val plannerService = mockk<PlannerService>(relaxed = true)
    private val selectedRecipeDataSource = SelectedRecipeDataSource()
    private val savedStateHandle = mockk<SavedStateHandle>()
    private lateinit var viewModel: PlannerEditViewModel

    @Before
    fun setup() {
        val recipe = Recipe(1, "Test Recipe", "Test Description", 1, 1, 1, false, Uri.EMPTY)
        coEvery { recipeService.getRecipeStream(any()) } returns flowOf(recipe)

        every { savedStateHandle.get<String>(any()) } returns "2024-01-01"

        selectedRecipeDataSource.updateRecipeId(1L)

        viewModel = PlannerEditViewModel(
            selectedRecipeDataSource,
            savedStateHandle,
            recipeService,
            plannerService
        )
    }

    @Test
    fun testUpdateType() = runBlocking {
        // Arrange
        val expectedMeal = Meal.LUNCH

        // Act
        viewModel.updateType(expectedMeal)

        // Assert
        val actualMeal = viewModel.uiState.value.selectedType
        assertEquals(expectedMeal, actualMeal)
    }

    @Test
    fun testInsertPlannerRecipe() = runBlocking {
        viewModel.insertPlannerRecipe()

        coVerify { plannerService.insertPlannerRecipe(any()) }
    }

    @Test
    fun testDeletePlannerRecipe() = runBlocking {
        // Arrange
        val expectedRecipe = PlannerRecipe(date = "2024-04-20", recipeId = 1, type = Meal.BREAKFAST)

        // Act
        viewModel.deletePlannerRecipe(expectedRecipe)

        // Assert
        coVerify { plannerService.deletePlannerRecipe(expectedRecipe) }
    }
}
