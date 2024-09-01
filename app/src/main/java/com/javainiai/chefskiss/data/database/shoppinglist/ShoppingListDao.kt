package com.javainiai.chefskiss.data.database.shoppinglist

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopRecipe)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ShopIngredient)

    @Query("SELECT * from shopping_list")
    fun getShoppingList(): Flow<List<ShopRecipe>>

    @Query("SELECT * from shopping_checked_ingredients")
    fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>>

    @Delete
    suspend fun delete(item: ShopIngredient)

    @Delete
    suspend fun delete(item: ShopRecipe)

}