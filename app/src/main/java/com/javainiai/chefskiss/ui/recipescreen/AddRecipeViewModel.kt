package com.javainiai.chefskiss.ui.recipescreen

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val tag: String,
    val tags: List<Tag>,
    val directions: String,
    val tagRemoveMode: Boolean
)

class AddRecipeViewModel(private val recipesRepository: RecipesRepository) : ViewModel() {
    private var existingRecipe: Recipe? = null
    private var _uiState =
        MutableStateFlow(
            AddRecipeUiState(
                "",
                "",
                "",
                Uri.EMPTY,
                IngredientDisplay("", "", ""),
                listOf(),
                "",
                listOf(),
                "",
                false
            )
        )
    val uiState = _uiState.asStateFlow()

    val tags: StateFlow<List<Tag>> = recipesRepository
        .getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    val snackbarHostState = SnackbarHostState()

    private var messageInProgress: Job? = null
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

    fun updateTag(tag: String) {
        _uiState.update { currentState ->
            currentState.copy(
                tag = tag
            )
        }
    }

    fun addTag() {
        // don't add empty tags...
        if (_uiState.value.tag == "")
            return

        viewModelScope.launch {
            recipesRepository.insertTag(Tag(title = _uiState.value.tag))
            updateTag("")
        }
    }

    fun removeTag(tag: Tag) {
        viewModelScope.launch {
            recipesRepository.deleteTag(tag)
            updateTags(_uiState.value.tags - tag)
        }
    }

    fun updateTagRemoveMode(mode: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                tagRemoveMode = mode
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

    fun initializeWithExistingRecipe(recipe: Recipe?){
        recipe?.let {
            existingRecipe = it
            _uiState.value = AddRecipeUiState(
                title = it.title,
                cookingTime = it.cookingTime.toString(),
                servings = it.servings.toString(),
                imageUri = it.imagePath,
                ingredient = IngredientDisplay("", "", ""),
                ingredients = listOf(),
                tag = "",
                tags = listOf(),
                directions = it.description,
                tagRemoveMode = false
            )
        }
    }

    suspend fun saveToDatabase(): Boolean {
        if (validateEntries()) {
            if(existingRecipe != null) {
                existingRecipe?.let { existingRecipe ->
                    val updatedRecipe = existingRecipe.copy(
                        title = uiState.value.title,
                        description = uiState.value.directions,
                        cookingTime = uiState.value.cookingTime.toIntOrNull() ?: 0,
                        servings = uiState.value.servings.toIntOrNull() ?: 0,
                        imagePath = uiState.value.imageUri
                    )
                    val list = mutableListOf<Ingredient>()
                    for (i in uiState.value.ingredients) {
                        val ingredient = Ingredient(
                            recipeId = existingRecipe.id,
                            name = i.title,
                            size = i.amount.toFloatOrNull() ?: 0f,
                            unit = i.units
                        )
                        list.add(ingredient)
                    }
                    recipesRepository.updateRecipeWithIngredientsAndTags(updatedRecipe, list, uiState.value.tags)
                }
            } else {
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

                    recipesRepository.insertRecipeWithIngredientsAndTags(recipe, list, tags)
                }
            }
        } else {
            return false
        }
        return true
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}