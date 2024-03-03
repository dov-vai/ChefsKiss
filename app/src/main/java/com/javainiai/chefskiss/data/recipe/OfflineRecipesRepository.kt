package com.javainiai.chefskiss.data.recipe

import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.ingredient.IngredientDao
import kotlinx.coroutines.flow.Flow

class OfflineRecipesRepository(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao
) : RecipesRepository {
    override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    override fun getRecipeStream(id: Int): Flow<Recipe?> = recipeDao.getRecipe(id)
    override fun getRecentRecipes(): Flow<List<Recipe>> = recipeDao.getRecentRecipes()
    override suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insert(recipe)
    override suspend fun insertRecipeWithIngredients(
        recipe: Recipe,
        ingredients: List<Ingredient>
    ) {
        val recipeId = insertRecipe(recipe)

        for (ingredient in ingredients) {
            ingredientDao.insert(ingredient.copy(recipeId = recipeId.toInt()))
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
}