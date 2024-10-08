package com.javainiai.chefskiss.data.database.recipe

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

    @Delete
    suspend fun delete(item: RecipeTagCrossRef)

    @Update
    suspend fun update(item: Recipe)

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
}