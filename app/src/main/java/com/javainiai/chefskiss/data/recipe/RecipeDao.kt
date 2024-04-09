package com.javainiai.chefskiss.data.recipe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Recipe): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: RecipeTagCrossRef)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopRecipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopIngredient)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PlannerRecipe)

    @Delete
    suspend fun delete(item: ShopIngredient)

    @Delete
    suspend fun delete(item: ShopRecipe)

    @Delete
    suspend fun delete(item: PlannerRecipe)

    @Update
    suspend fun update(item: Recipe)

    @Update
    suspend fun update(item: PlannerRecipe)

    @Delete
    suspend fun delete(item: Recipe)

    @Query("SELECT * from recipes WHERE id = :id")
    fun getRecipe(id: Long): Flow<Recipe>

    @Transaction
    @Query("SELECT * from recipes WHERE id = :id")
    fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients>

    @Transaction
    @Query("SELECT * from recipes WHERE id in (:recipeIds)")
    fun getRecipesWithIngredients(recipeIds: List<Long>): Flow<List<RecipeWithIngredients>>

    @Transaction
    @Query("SELECT * from recipes WHERE id = :id")
    fun getRecipeWithTags(id: Long): Flow<RecipeWithTags>

    @Query("SELECT * from recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * from shopping_list")
    fun getShoppingList(): Flow<List<ShopRecipe>>

    @Query("SELECT * from shopping_checked_ingredients")
    fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>>

    @Query(
        "SELECT * from recipes ORDER BY " +
                "CASE WHEN :isAsc = 1 THEN id END ASC," +
                "CASE WHEN :isAsc = 0 THEN id END DESC"
    )
    fun getRecipesByTimeAdded(isAsc: Boolean): Flow<List<Recipe>>

    @Query(
        "SELECT * from recipes ORDER BY " +
                "CASE WHEN :isAsc = 1 THEN cookingTime END ASC," +
                "CASE WHEN :isAsc = 0 THEN cookingTime END DESC"
    )
    fun getRecipesByCookingTime(isAsc: Boolean): Flow<List<Recipe>>

    @Query(
        "SELECT * from recipes ORDER BY " +
                "CASE WHEN :isAsc = 1 THEN rating END ASC," +
                "CASE WHEN :isAsc = 0 THEN rating END DESC"
    )
    fun getRecipesByRating(isAsc: Boolean): Flow<List<Recipe>>

    @Query(
        "SELECT * from recipes ORDER BY " +
                "CASE WHEN :isAsc = 1 THEN servings END ASC," +
                "CASE WHEN :isAsc = 0 THEN servings END DESC"
    )
    fun getRecipesByServings(isAsc: Boolean): Flow<List<Recipe>>

    @RawQuery(observedEntities = [Recipe::class])
    fun query(query: SupportSQLiteQuery): Flow<List<Recipe>>

    @Query("SELECT recipeId from recipes_tags WHERE tagId IN (:tagIds)")
    fun getRecipeIdsByTagIds(tagIds: List<Long>): List<Long>

    @Query("SELECT * from recipes WHERE id in (:recipeIds)")
    fun getRecipesByIds(recipeIds: List<Long>): Flow<List<Recipe>>

    /**
     * @param date Must be in format yyyy-mm-dd
     */
    @Query("SELECT * from planner_recipes WHERE date=:date")
    fun getPlannerRecipesByDate(date: String): Flow<List<PlannerRecipe>>

    @Transaction
    @Query("SELECT * from planner_recipes WHERE date=:date")
    fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>>

    @Transaction
    @Query("SELECT * from planner_recipes WHERE date IN (:dates)")
    fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>>
}