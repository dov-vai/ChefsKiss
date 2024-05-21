package com.javainiai.chefskiss

import android.content.Context
import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ApplicationProvider
import com.javainiai.chefskiss.data.enums.CookingUnit
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.recipe.RecipeWithTags
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeViewModel
import com.javainiai.chefskiss.ui.recipescreen.IngredientDisplay
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddRecipeViewModelTest {
    private val recipesRepository = mockk<RecipesRepository>(relaxed = true)
    private val savedStateHandle = mockk<SavedStateHandle>(relaxed = true)
    private lateinit var viewModel: AddRecipeViewModel
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        every { savedStateHandle.get<Long>(any()) } returns null
        viewModel = AddRecipeViewModel(context, savedStateHandle, recipesRepository)
    }

    @Test
    fun addRecipeViewModel_updateTitle_uiStateUpdates() = runBlocking {
        val title = "Test Title"
        viewModel.updateTitle(title)
        Assert.assertEquals(title, viewModel.uiState.first().title)
    }

    @Test
    fun addRecipeViewModel_updateCookingTime_uiStateUpdates() = runBlocking {
        val time = "30"
        viewModel.updateCookingTime(time)
        Assert.assertEquals(time, viewModel.uiState.first().cookingTime)
    }

    @Test
    fun addRecipeViewModel_updateServings_uiStateUpdates() = runBlocking {
        val servings = "4"
        viewModel.updateServings(servings)
        Assert.assertEquals(servings, viewModel.uiState.first().servings)
    }

    @Test
    fun addRecipeViewModel_updateImageUri_uiStateUpdates() = runBlocking {
        val uri = mockk<Uri>()
        viewModel.updateImageUri(uri)
        Assert.assertEquals(uri, viewModel.uiState.first().imageUri)
    }

    @Test
    fun addRecipeViewModel_updateIngredient_uiStateUpdates() = runBlocking {
        val ingredient = IngredientDisplay("Test Ingredient", "1", CookingUnit.Cup)
        viewModel.updateIngredient(ingredient)
        Assert.assertEquals(ingredient, viewModel.uiState.first().ingredient)
    }

    @Test
    fun addRecipeViewModel_updateIngredients_uiStateUpdates() = runBlocking {
        val ingredients = listOf(IngredientDisplay("Test Ingredient", "1", CookingUnit.Cup))
        viewModel.updateIngredients(ingredients)
        Assert.assertEquals(ingredients, viewModel.uiState.first().ingredients)
    }

    @Test
    fun addRecipeViewModel_updateTag_uiStateUpdates() = runBlocking {
        val tag = "Test Tag"
        viewModel.updateTag(tag)
        Assert.assertEquals(tag, viewModel.uiState.first().tag)
    }

    @Test
    fun addRecipeViewModel_addTag_insertsIntoDatabase() = runBlocking {
        val tag = "Test Tag"
        viewModel.updateTag(tag)
        viewModel.addTag()
        coVerify { recipesRepository.insertTag(Tag(title = tag)) }
    }

    @Test
    fun addRecipeViewModel_removeTag_removesFromDatabase() = runBlocking {
        val tag = Tag(title = "Test Tag")
        viewModel.removeTag(tag)
        coVerify { recipesRepository.deleteTag(tag) }
    }

    @Test
    fun addRecipeViewModel_updateTagRemoveMode_uiStateUpdates() = runBlocking {
        val mode = true
        viewModel.updateTagRemoveMode(mode)
        Assert.assertEquals(mode, viewModel.uiState.first().tagRemoveMode)
    }

    @Test
    fun addRecipeViewModel_updateTags_uiStateUpdates() = runBlocking {
        val tags = listOf(Tag(title = "Test Tag"))
        viewModel.updateTags(tags)
        Assert.assertEquals(tags, viewModel.uiState.first().tags)
    }

    @Test
    fun addRecipeViewModel_updateDirections_uiStateUpdates() = runBlocking {
        val directions = "Test Directions"
        viewModel.updateDirections(directions)
        Assert.assertEquals(directions, viewModel.uiState.first().directions)
    }

    @Test
    fun addRecipeViewModel_updateEditingIngredient_uiStateUpdates() = runBlocking {
        val ingredient = IngredientDisplay("Test Ingredient", "1", CookingUnit.Cup)
        viewModel.updateEditingIngredient(ingredient)
        Assert.assertEquals(ingredient, viewModel.uiState.first().editingIngredient)
    }

    @Test
    fun addRecipeViewModel_inizitializeEditRecipe_uiStateUpdates() = runBlocking {
        val recipeId = 1L
        val recipe = Recipe(recipeId, "Test Recipe", "Test Description", 30, 4, 5, false, Uri.EMPTY)
        every { recipesRepository.getRecipeWithTags(any()) } returns flowOf(RecipeWithTags(recipe, listOf()))
        every { recipesRepository.getRecipeWithIngredients(any()) } returns flowOf(RecipeWithIngredients(recipe, listOf()))
        viewModel.initializeEditRecipe(recipeId)
        val uiState = viewModel.uiState.first()
        Assert.assertEquals("Test Recipe", uiState.title)
        Assert.assertEquals("30", uiState.cookingTime)
        Assert.assertEquals("4", uiState.servings)
        Assert.assertEquals(5, uiState.rating)
        Assert.assertEquals(false, uiState.favorite)
        Assert.assertEquals(Uri.EMPTY, uiState.imageUri)
        Assert.assertEquals("Test Description", uiState.directions)
    }

    @Test
    fun addRecipeViewModel_saveToDatabase_returnsTrue() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("4")
        viewModel.updateDirections("Test Description")
        val result = viewModel.saveToDatabase()
        Assert.assertTrue(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseEmptyTitle_returnsFalse() = runBlocking {
        viewModel.updateTitle("")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("4")
        viewModel.updateDirections("Test Description")
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseEmptyCookingTime_returnsFalse() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("")
        viewModel.updateServings("4")
        viewModel.updateDirections("Test Description")
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseEmptyServings_returnsFalse() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("")
        viewModel.updateDirections("Test Description")
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseEmptyDirections_returnsTrue() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("4")
        viewModel.updateDirections("")
        val result = viewModel.saveToDatabase()
        Assert.assertTrue(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseNonIntegerServings_returnsFalse() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("Three")
        viewModel.updateDirections("Test Description")
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseNonIntegerIngredientAmount_returnsFalse() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("3")
        viewModel.updateDirections("Test Description")
        viewModel.updateIngredients(listOf(IngredientDisplay("Test Ingredient", "Three", CookingUnit.Kilogram)))
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }

    @Test
    fun addRecipeViewModel_saveToDatabaseIntegerIngredientAmount_returnsTrue() = runBlocking {
        viewModel.updateTitle("Test Recipe")
        viewModel.updateCookingTime("30")
        viewModel.updateServings("Three")
        viewModel.updateDirections("Test Description")
        viewModel.updateIngredient((IngredientDisplay("Test Ingredient", "3", CookingUnit.Kilogram)))
        val result = viewModel.saveToDatabase()
        Assert.assertFalse(result)
    }
}