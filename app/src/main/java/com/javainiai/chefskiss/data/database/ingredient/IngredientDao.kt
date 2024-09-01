package com.javainiai.chefskiss.data.database.ingredient

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface IngredientDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Ingredient)

    @Update
    suspend fun update(item: Ingredient)

    @Delete
    suspend fun delete(item: Ingredient)

    @Query("SELECT * from ingredients WHERE id = :id")
    fun getIngredient(id: Long): Flow<Ingredient>

    @Query("SELECT * from ingredients ORDER BY name ASC")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * from ingredients WHERE recipeId = :recipeId")
    fun getAllRecipeIngredients(recipeId: Long): Flow<List<Ingredient>>
}