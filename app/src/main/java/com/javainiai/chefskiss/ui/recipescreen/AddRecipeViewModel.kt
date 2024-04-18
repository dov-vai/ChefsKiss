package com.javainiai.chefskiss.ui.recipescreen

import android.net.Uri
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val rating: Int,
    val favorite: Boolean,
    val imageUri: Uri,
    val ingredient: IngredientDisplay,
    val editingIngredient: IngredientDisplay?,
    val ingredients: List<IngredientDisplay>,
    val tag: String,
    val tags: List<Tag>,
    val directions: String,
    val tagRemoveMode: Boolean
)

class AddRecipeViewModel(
    savedStateHandle: SavedStateHandle,
    private val recipesRepository: RecipesRepository
) : ViewModel() {
    private val editRecipeId: Long? = savedStateHandle[EditRecipeDestination.editRecipeIdArg]

    private var _uiState =
        MutableStateFlow(
            AddRecipeUiState(
                "",
                "",
                "",
                0,
                false,
                Uri.EMPTY,
                IngredientDisplay("", "", ""),
                null,
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

    init {
        editRecipeId?.let {
            initializeEditRecipe(it)
        }
    }

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

    fun updateEditingIngredient(ingredient: IngredientDisplay?) {
        _uiState.update { currentState ->
            currentState.copy(
                editingIngredient = ingredient
            )
        }
    }

    // TODO: show validation messages to the user
    private fun validateEntries(): Boolean {
        with(uiState.value) {

        }
        return true
    }

    private fun initializeEditRecipe(recipeId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val recipeWithTags = recipesRepository.getRecipeWithTags(recipeId)
            val recipeWithIngredients = recipesRepository.getRecipeWithIngredients(recipeId)
            // if we are loading it, it should exist, so assert non-null
            val recipe = recipeWithTags.first()!!.recipe
            val tags = recipeWithTags.first()!!.tags
            val ingredients = recipeWithIngredients.first()!!.ingredients

            _uiState.update {
                AddRecipeUiState(
                    title = recipe.title,
                    cookingTime = recipe.cookingTime.toString(),
                    servings = recipe.servings.toString(),
                    rating = recipe.rating,
                    favorite = recipe.favorite,
                    imageUri = recipe.imagePath,
                    ingredient = IngredientDisplay("", "", ""),
                    editingIngredient = null,
                    ingredients = ingredients.map {
                        IngredientDisplay(
                            it.name,
                            it.size.toString(),
                            it.unit
                        )
                    },
                    tag = "",
                    tags = tags,
                    directions = recipe.description,
                    tagRemoveMode = false
                )
            }
        }
    }

    suspend fun saveToDatabase(): Boolean {
        if (validateEntries()) {
            with(uiState.value) {
                val recipe = Recipe(
                    id = editRecipeId ?: 0,
                    title = title,
                    description = directions,
                    cookingTime = cookingTime.toIntOrNull() ?: 0,
                    servings = servings.toIntOrNull() ?: 0,
                    rating = rating,
                    favorite = favorite,
                    imagePath = imageUri
                )

                val list = mutableListOf<Ingredient>()
                for (i in ingredients) {
                    val ingredient = Ingredient(
                        recipeId = editRecipeId ?: 0,
                        name = i.title,
                        size = i.amount.toFloatOrNull() ?: 0f,
                        unit = i.units
                    )

                    list.add(ingredient)
                }

                if (editRecipeId != null) {
                    recipesRepository.updateRecipeWithIngredientsAndTags(recipe, list, tags)
                } else {
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