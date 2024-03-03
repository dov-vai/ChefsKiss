package com.javainiai.chefskiss.data.recipe

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.javainiai.chefskiss.data.ingredient.Ingredient
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Recipe): Long

    @Transaction
    @Insert
    suspend fun insert(item: Recipe, ingredients: List<Ingredient>)

    @Update
    suspend fun update(item: Recipe)

    @Delete
    suspend fun delete(item: Recipe)

    @Query("SELECT * from recipes WHERE id = :id")
    fun getRecipe(id: Int): Flow<Recipe>

    @Transaction
    @Query("SELECT * from recipes")
    fun getAllRecipes(): Flow<List<Recipe>>

    @Query("SELECT * from recipes ORDER BY id DESC")
    fun getRecentRecipes(): Flow<List<Recipe>>
}