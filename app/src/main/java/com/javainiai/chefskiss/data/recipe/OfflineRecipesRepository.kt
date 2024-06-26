package com.javainiai.chefskiss.data.recipe

import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.javainiai.chefskiss.data.enums.Sort
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.ingredient.IngredientDao
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.data.tag.TagDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class OfflineRecipesRepository(
    private val recipeDao: RecipeDao,
    private val ingredientDao: IngredientDao,
    private val tagDao: TagDao
) : RecipesRepository {
    override fun getAllTags(): Flow<List<Tag>> = tagDao.getAllTags()
    override fun getAllRecipesStream(): Flow<List<Recipe>> = recipeDao.getAllRecipes()
    override fun getShoppingList(): Flow<List<ShopRecipe>> = recipeDao.getShoppingList()
    override fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>> =
        recipeDao.getShoppingCheckedIngredients()

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

    override fun getPlannerRecipesByDate(date: String): Flow<List<PlannerRecipe>> =
        recipeDao.getPlannerRecipesByDate(date)

    override fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>> =
        recipeDao.getPlannerRecipesWithRecipes(date)

    override fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>> =
        recipeDao.getPlannerRecipesWithRecipes(dates)

    override fun query(query: SupportSQLiteQuery): Flow<List<Recipe>> = recipeDao.query(query)
    override fun query(
        recipeName: String,
        rating: Int,
        isAsc: Boolean,
        sortingMethod: Sort,
        tags: List<Tag>,
        favorite: Boolean
    ): Flow<List<Recipe>> {

        var query = "SELECT * FROM recipes WHERE rating >= ? AND title LIKE ?"

        if (favorite) {
            query += " AND favorite = true"
        }

        if (tags.isNotEmpty()) {
            val tagIds = tags.map { it.id }.joinToString()
            query += " AND id IN (SELECT recipeId from recipes_tags WHERE tagId IN ($tagIds))"
        }

        val sort = when (sortingMethod) {
            Sort.COOKING_TIME -> "cookingTime"
            Sort.PORTION -> "servings"
            Sort.RATING -> "rating"
            Sort.ADDED -> "id"
        }

        val order = if (isAsc) "ASC" else "DESC"

        query += " ORDER BY $sort $order"

        return recipeDao.query(SimpleSQLiteQuery(query, arrayOf(rating, "%$recipeName%")))
    }


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

    override suspend fun updateRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    ) {
        updateRecipe(recipe)

        // have to be cleared before updating
        val ingredientsToRemove = getRecipeWithIngredients(recipe.id).first()!!.ingredients
        val tagsToRemove = getRecipeWithTags(recipe.id).first()!!.tags

        for (ingredient in ingredientsToRemove) {
            ingredientDao.delete(ingredient)
        }
        for (tag in tagsToRemove) {
            recipeDao.delete(RecipeTagCrossRef(recipe.id, tag.id))
        }

        for (ingredient in ingredients) {
            ingredientDao.insert(ingredient)
        }
        for (tag in tags) {
            recipeDao.insert(RecipeTagCrossRef(recipe.id, tag.id))
        }
    }

    override suspend fun deleteRecipe(recipe: Recipe) = recipeDao.delete(recipe)
    override suspend fun updateRecipe(recipe: Recipe) = recipeDao.update(recipe)
    override suspend fun insertShopRecipe(recipe: ShopRecipe) = recipeDao.insert(recipe)
    override suspend fun deleteShopRecipe(recipe: ShopRecipe) = recipeDao.delete(recipe)
    override suspend fun insertShopIngredient(item: ShopIngredient) = recipeDao.insert(item)
    override suspend fun deleteShopIngredient(item: ShopIngredient) = recipeDao.delete(item)
    override suspend fun insertPlannerRecipe(item: PlannerRecipe) = recipeDao.insert(item)
    override suspend fun updatePlannerRecipe(item: PlannerRecipe) = recipeDao.update(item)
    override suspend fun deletePlannerRecipe(item: PlannerRecipe) = recipeDao.delete(item)
    override suspend fun deleteRecipeTag(item: RecipeTagCrossRef) = recipeDao.delete(item)
}