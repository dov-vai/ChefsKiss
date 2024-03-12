package com.javainiai.chefskiss.data.recipe

import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getAllTags(): Flow<List<Tag>>
    fun getAllRecipesStream(): Flow<List<Recipe>>
    fun getRecipeStream(id: Long): Flow<Recipe?>
    fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?>
    fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?>
    fun getRecentRecipes(): Flow<List<Recipe>>
    fun getRecipesByCookingTime(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipesByRating(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipesByServings(isAsc: Boolean): Flow<List<Recipe>>
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    suspend fun insertRecipe(recipe: Recipe): Long
    suspend fun insertRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    )

    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
}