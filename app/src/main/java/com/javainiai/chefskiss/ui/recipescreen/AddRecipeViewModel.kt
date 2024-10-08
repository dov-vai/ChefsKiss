package com.javainiai.chefskiss.ui.recipescreen

import android.content.Context
import android.net.Uri
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.enums.CookingUnit
import com.javainiai.chefskiss.ui.components.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class IngredientDisplay(
    val title: String,
    val amount: String,
    val units: CookingUnit
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
    private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val recipeService: RecipeService
) : BaseViewModel() {
    private val editRecipeId: Long? = savedStateHandle[EditRecipeDestination.editRecipeIdArg]

    private var _uiState =
        MutableStateFlow(
            AddRecipeUiState(
                "",
                "0",
                "1",
                0,
                false,
                Uri.EMPTY,
                IngredientDisplay("", "", CookingUnit.Gram),
                null,
                listOf(),
                "",
                listOf(),
                "",
                false
            )
        )
    val uiState = _uiState.asStateFlow()

    val tags: StateFlow<List<Tag>> = recipeService
        .getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    init {
        editRecipeId?.let {
            viewModelScope.launch { initializeEditRecipe(it) }
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

    suspend fun addTag() {
        // don't add empty tags...
        if (_uiState.value.tag == "")
            return

        recipeService.insertTag(Tag(title = _uiState.value.tag))
        updateTag("")
    }

    suspend fun removeTag(tag: Tag) {
        recipeService.deleteTag(tag)
        updateTags(_uiState.value.tags - tag)
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

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    public suspend fun initializeEditRecipe(recipeId: Long) {
        withContext(Dispatchers.IO) {
            val recipeWithTags = recipeService.getRecipeWithTags(recipeId)
            val recipeWithIngredients = recipeService.getRecipeWithIngredients(recipeId)
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
                    ingredient = IngredientDisplay("", "", CookingUnit.Gram),
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

    private fun validateEntries(): Boolean {
        with(uiState.value) {
            title.ifEmpty {
                showMessage(context.getString(R.string.title_should_not_be_empty))
                return false
            }
            cookingTime.ifEmpty {
                showMessage(context.getString(R.string.cooking_time_should_not_be_empty))
                return false
            }
            if (cookingTime.toIntOrNull() == null) {
                showMessage(
                    context.getString(
                        R.string.cooking_time_should_be_a_whole_number_ex_1_not,
                        cookingTime
                    )
                )
                return false
            }
            servings.ifEmpty {
                showMessage(context.getString(R.string.serving_size_should_not_be_empty))
                return false
            }
            if (servings.toIntOrNull() == null) {
                showMessage(
                    context.getString(
                        R.string.serving_size_should_be_a_whole_number_ex_1_not,
                        servings
                    )
                )
                return false
            }
            for (i in ingredients) {
                if (i.amount.toFloatOrNull() == null) {
                    showMessage(
                        context.getString(
                            R.string.ingredients_amount_should_be_a_number_ex_1_1_not,
                            i.title,
                            i.amount
                        )
                    )
                    return false
                }
            }
        }
        return true
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
                    recipeService.updateRecipeWithIngredientsAndTags(recipe, list, tags)
                } else {
                    recipeService.insertRecipeWithIngredientsAndTags(recipe, list, tags)
                }
                return true
            }
        }
        return false
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}