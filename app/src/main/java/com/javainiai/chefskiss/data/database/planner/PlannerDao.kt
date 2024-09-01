package com.javainiai.chefskiss.data.database.planner

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PlannerRecipe)

    @Update
    suspend fun update(item: PlannerRecipe)

    @Delete
    suspend fun delete(item: PlannerRecipe)

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