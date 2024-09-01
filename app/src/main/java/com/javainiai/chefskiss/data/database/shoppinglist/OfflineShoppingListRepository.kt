package com.javainiai.chefskiss.data.database.shoppinglist

import kotlinx.coroutines.flow.Flow

class OfflineShoppingListRepository(private val shoppingListDao: ShoppingListDao) :
    ShoppingListRepository {

    override fun getShoppingList(): Flow<List<ShopRecipe>> = shoppingListDao.getShoppingList()
    override fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>> =
        shoppingListDao.getShoppingCheckedIngredients()

    override suspend fun insertShopRecipe(recipe: ShopRecipe) = shoppingListDao.insert(recipe)
    override suspend fun deleteShopRecipe(recipe: ShopRecipe) = shoppingListDao.delete(recipe)
    override suspend fun insertShopIngredient(item: ShopIngredient) = shoppingListDao.insert(item)
    override suspend fun deleteShopIngredient(item: ShopIngredient) = shoppingListDao.delete(item)
}