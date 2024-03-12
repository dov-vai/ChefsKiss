package com.javainiai.chefskiss.data.recipe

import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.ingredient.IngredientDao
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.data.tag.TagDao
import kotlinx.coroutines.flow.Flow

class OfflineRecipesRepository(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val tagDao: TagDao
) : RecipesRepository {
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    override fun getRecipeStream(id: Long): Flow<Recipe?> = recipeDao.getRecipe(id)
    override fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?> =
        recipeDao.getRecipeWithIngredients(id)

    override fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?> =
        recipeDao.getRecipeWithTags(id)

    override fun getRecentRecipes(): Flow<List<Recipe>> = recipeDao.getRecentRecipes()
    override fun getRecipesByCookingTime(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByCookingTime(isAsc)

    override fun getRecipesByRating(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByRating(isAsc)

    override fun getRecipesByServings(isAsc: Boolean): Flow<List<Recipe>> =
        recipeDao.getRecipesByServings(isAsc)

    override suspend fun insertTag(tag: Tag): Long = tagDao.insert(tag)
    override suspend fun deleteTag(tag: Tag) = tagDao.delete(tag)
    override suspend fun insertRecipe(recipe: Recipe): Long = recipeDao.insert(recipe)
    override suspend fun insertRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    ) {
        val recipeId = insertRecipe(recipe)

        for (ingredient in ingredients) {
            ingredientDao.insert(ingredient.copy(recipeId = recipeId))
        }

        for (tag in tags) {
            recipeDao.insert(RecipeTagCrossRef(recipeId, tag.id))
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
}