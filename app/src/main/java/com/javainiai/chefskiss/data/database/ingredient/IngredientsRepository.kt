package com.javainiai.chefskiss.data.database.ingredient

import kotlinx.coroutines.flow.Flow

interface IngredientsRepository {
    fun getAllIngredientsStream(): Flow<List<Ingredient>>
    fun getIngredientStream(id: Long): Flow<Ingredient?>
    fun getAllRecipeIngredients(recipeId: Long): Flow<List<Ingredient>>
    suspend fun insertIngredient(ingredient: Ingredient)
    suspend fun deleteIngredient(ingredient: Ingredient)
    suspend fun updateIngredient(ingredient: Ingredient)
}