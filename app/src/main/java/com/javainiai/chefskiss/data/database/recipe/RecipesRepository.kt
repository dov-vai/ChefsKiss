package com.javainiai.chefskiss.data.database.recipe

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getAllRecipesStream(): Flow<List<Recipe>>
    fun getRecipesByIds(recipeIds: List<Long>): Flow<List<Recipe>>
    fun getRecipeStream(id: Long): Flow<Recipe?>
    fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?>
    fun getRecipesWithIngredients(recipeIds: List<Long>): Flow<List<RecipeWithIngredients>>
    fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?>
    fun getRecipesByTimeAdded(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipesByCookingTime(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipesByRating(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipesByServings(isAsc: Boolean): Flow<List<Recipe>>
    fun getRecipeIdsByTagIds(tagIds: List<Long>): List<Long>
    fun query(query: SupportSQLiteQuery): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe): Long
    suspend fun insertRecipeTag(recipeTag: RecipeTagCrossRef)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun deleteRecipeTag(item: RecipeTagCrossRef)
}