package com.javainiai.chefskiss.data.recipe

import com.javainiai.chefskiss.data.ingredient.Ingredient
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getAllRecipesStream(): Flow<List<Recipe>>
    fun getRecipeStream(id: Int): Flow<Recipe?>
    fun getRecentRecipes(): Flow<List<Recipe>>
    suspend fun insertRecipe(recipe: Recipe): Long
    suspend fun insertRecipeWithIngredients(recipe: Recipe, ingredients: List<Ingredient>)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
}