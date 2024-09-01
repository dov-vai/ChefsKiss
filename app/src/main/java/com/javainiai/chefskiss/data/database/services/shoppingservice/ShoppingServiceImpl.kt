package com.javainiai.chefskiss.data.database.services.shoppingservice

import com.javainiai.chefskiss.data.database.shoppinglist.ShopIngredient
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import com.javainiai.chefskiss.data.database.shoppinglist.ShoppingListRepository
import kotlinx.coroutines.flow.Flow

class ShoppingServiceImpl(private val shoppingListRepository: ShoppingListRepository) :
    ShoppingService {
    override fun getShoppingList(): Flow<List<ShopRecipe>> {
        return shoppingListRepository.getShoppingList()
    }

    override fun getShoppingCheckedIngredients(): Flow<List<ShopIngredient>> {
        return shoppingListRepository.getShoppingCheckedIngredients()
    }

    override suspend fun insertShopRecipe(recipe: ShopRecipe) {
        shoppingListRepository.insertShopRecipe(recipe)
    }

    override suspend fun deleteShopRecipe(recipe: ShopRecipe) {
        shoppingListRepository.deleteShopRecipe(recipe)
    }

    override suspend fun insertShopIngredient(item: ShopIngredient) {
        shoppingListRepository.insertShopIngredient(item)
    }

    override suspend fun deleteShopIngredient(item: ShopIngredient) {
        shoppingListRepository.deleteShopIngredient(item)
    }
}