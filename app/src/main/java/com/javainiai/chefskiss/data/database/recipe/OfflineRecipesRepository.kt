package com.javainiai.chefskiss.data.database.recipe

import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

class OfflineRecipesRepository(
    private val recipeDao: RecipeDao
) : RecipesRepository {
    override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getAllRecipes()

    override fun getRecipesByIds(recipeIds: List<Long>): Flow<List<Recipe>> =
        recipeDao.getRecipesByIds(recipeIds)

    override fun getRecipeStream(id: Long): Flow<Recipe?> = recipeDao.getRecipe(id)
    override fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?> =
        recipeDao.getRecipeWithIngredients(id)

    override fun getRecipesWithIngredients(recipeIds: List<Long>): Flow<List<RecipeWithIngredients>> =
        recipeDao.getRecipesWithIngredients(recipeIds)

    override fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?> =
        recipeDao.getRecipeWithTags(id)

    override fun getRecipesByTimeAdded(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByTimeAdded(isAsc)

    override fun getRecipesByCookingTime(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByCookingTime(isAsc)

    override fun getRecipesByRating(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByRating(isAsc)

    override fun getRecipesByServings(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByServings(isAsc)

    override fun getRecipeIdsByTagIds(tagIds: List<Long>): List<Long> =
        recipeDao.getRecipeIdsByTagIds(tagIds)

    override fun query(query: SupportSQLiteQuery): Flow<List<Recipe>> = recipeDao.query(query)
    override suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insert(recipe)
    override suspend fun insertRecipeTag(recipeTag: RecipeTagCrossRef) = recipeDao.insert(recipeTag)

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
    override suspend fun deleteRecipeTag(item: RecipeTagCrossRef) = recipeDao.delete(item)
}