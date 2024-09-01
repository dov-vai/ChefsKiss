package com.javainiai.chefskiss.data.database.services.shoppingservice

import com.javainiai.chefskiss.data.database.shoppinglist.ShopIngredient
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import kotlinx.coroutines.flow.Flow

interface ShoppingService {
    fun getShoppingList(): Flow<List<ShopRecipe>>
    fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>>
    suspend fun insertShopRecipe(recipe: ShopRecipe)
    suspend fun deleteShopRecipe(recipe: ShopRecipe)
    suspend fun insertShopIngredient(item: ShopIngredient)
    suspend fun deleteShopIngredient(item: ShopIngredient)
}