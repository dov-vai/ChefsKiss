package com.javainiai.chefskiss.data.recipe

import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getAllRecipesStream(): Flow<List<Recipe>>
    fun getRecipeStream(id: Int): Flow<Recipe?>
    fun getRecentRecipes(): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
}