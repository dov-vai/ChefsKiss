package com.javainiai.chefskiss.data.ingredient

import kotlinx.coroutines.flow.Flow

class OfflineIngredientsRepository(private val ingredientDao: IngredientDao) :
    IngredientsRepository {
    override fun getAllIngredientsStream(): Flow<List<Ingredient>> =
        ingredientDao.getAllIngredients()

    override fun getIngredientStream(id: Long): Flow<Ingredient?> = ingredientDao.getIngredient(id)
    override suspend fun insertIngredient(ingredient: Ingredient) = ingredientDao.insert(ingredient)
    override suspend fun deleteIngredient(ingredient: Ingredient) = ingredientDao.delete(ingredient)
    override suspend fun updateIngredient(ingredient: Ingredient) = ingredientDao.update(ingredient)
    override fun getAllRecipeIngredients(recipeId: Long): Flow<List<Ingredient>> =
        ingredientDao.getAllRecipeIngredients(recipeId)
}