package com.javainiai.chefskiss.ui.recipescreen

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class IngredientDisplay(
    val title: String,
    val amount: String,
    val units: String
)

data class AddRecipeUiState(
    val title: String,
    val cookingTime: String,
    val servings: String,
    val imageUri: Uri,
    val ingredient: IngredientDisplay,
    val ingredients: List<IngredientDisplay>,
    val tags: List<Tag>,
    val directions: String
)

class AddRecipeViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private var _uiState =
        MutableStateFlow(
            AddRecipeUiState(
                "",
                "",
                "",
                Uri.EMPTY,
                IngredientDisplay("", "", ""),
                listOf(),
                listOf(),
                ""
            )
        )
    val uiState = _uiState.asStateFlow()

    fun updateTitle(title: String) {
        _uiState.update { currentState ->
            currentState.copy(
                title = title
            )
        }
    }

    fun updateCookingTime(time: String) {
        _uiState.update { currentState ->
            currentState.copy(
                cookingTime = time
            )
        }
    }

    fun updateServings(servings: String) {
        _uiState.update { currentState ->
            currentState.copy(
                servings = servings
            )
        }
    }

    fun updateImageUri(uri: Uri) {
        _uiState.update { currentState ->
            currentState.copy(
                imageUri = uri
            )
        }
    }

    fun updateIngredient(ingredient: IngredientDisplay) {
        _uiState.update { currentState ->
            currentState.copy(
                ingredient = ingredient
            )
        }
    }

    fun updateIngredients(ingredients: List<IngredientDisplay>) {
        _uiState.update { currentState ->
            currentState.copy(
                ingredients = ingredients
            )
        }
    }

    fun updateTags(tags: List<Tag>) {
        _uiState.update { currentState ->
            currentState.copy(
                tags = tags
            )
        }
    }

    fun updateDirections(directions: String) {
        _uiState.update { currentState ->
            currentState.copy(
                directions = directions
            )
        }
    }

    // TODO: show validation messages to the user
    private fun validateEntries(): Boolean {
        with(uiState.value) {

        }
        return true
    }

    suspend fun saveToDatabase(): Boolean {
        if (validateEntries()) {
            with(uiState.value) {
                val recipe = Recipe(
                    title = title,
                    description = directions,
                    cookingTime = cookingTime.toIntOrNull() ?: 0,
                    servings = cookingTime.toIntOrNull() ?: 0,
                    rating = 0,
                    favorite = false,
                    imagePath = imageUri
                )

                val list = mutableListOf<Ingredient>()
                for (i in ingredients) {
                    val ingredient = Ingredient(
                        recipeId = 0,
                        name = i.title,
                        size = i.amount.toFloatOrNull() ?: 0f,
                        unit = i.units
                    )

                    list.add(ingredient)
                }

                recipesRepository.insertRecipeWithIngredients(recipe, list)
            }
        } else {
            return false
        }
        return true
    }
}