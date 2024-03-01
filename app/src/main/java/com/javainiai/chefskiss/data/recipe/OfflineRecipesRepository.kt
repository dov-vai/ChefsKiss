package com.javainiai.chefskiss.data.recipe

import kotlinx.coroutines.flow.Flow

class OfflineRecipesRepository(private val recipeDao: RecipeDao) : RecipesRepository {
    override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    override fun getRecipeStream(id: Int): Flow<Recipe?> = recipeDao.getRecipe(id)
    override fun getRecentRecipes(): Flow<List<Recipe>> = recipeDao.getRecentRecipes()
    override suspend fun insertRecipe(recipe: Recipe) = recipeDao.insert(recipe)
    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
}