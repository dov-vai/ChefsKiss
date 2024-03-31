package com.javainiai.chefskiss.data.recipe

import androidx.sqlite.db.SupportSQLiteQuery
import com.javainiai.chefskiss.data.enums.Sort
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.flow.Flow

interface RecipesRepository {
    fun getAllTags(): Flow<List<Tag>>
    fun getAllRecipesStream(): Flow<List<Recipe>>
    fun getShoppingList(): Flow<List<ShopRecipe>>
    fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>>
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
    fun getPlannerRecipesByDate(date: String): Flow<List<PlannerRecipe>>
    fun query(query: SupportSQLiteQuery): Flow<List<Recipe>>
    fun query(
        recipeName: String,
        rating: Int,
        isAsc: Boolean,
        sortingMethod: Sort,
        tags: List<Tag>,
        favorite: Boolean
    ): Flow<List<Recipe>>

    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    suspend fun insertRecipe(recipe: Recipe): Long
    suspend fun insertRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    )

    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    suspend fun insertShopRecipe(recipe: ShopRecipe)
    suspend fun deleteShopRecipe(recipe: ShopRecipe)
    suspend fun insertShopIngredient(item: ShopIngredient)
    suspend fun deleteShopIngredient(item: ShopIngredient)
    suspend fun insertPlannerRecipe(item: PlannerRecipe)
    suspend fun updatePlannerRecipe(item: PlannerRecipe)
    suspend fun deletePlannerRecipe(item: PlannerRecipe)
}