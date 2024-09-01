package com.javainiai.chefskiss.data.database.shoppinglist

import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {
    fun getShoppingList(): Flow<List<ShopRecipe>>
    fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>>
    suspend fun insertShopRecipe(recipe: ShopRecipe)
    suspend fun deleteShopRecipe(recipe: ShopRecipe)
    suspend fun insertShopIngredient(item: ShopIngredient)
    suspend fun deleteShopIngredient(item: ShopIngredient)
}