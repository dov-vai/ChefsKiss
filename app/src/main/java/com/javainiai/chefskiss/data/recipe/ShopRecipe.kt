package com.javainiai.chefskiss.data.recipe

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShopRecipe(
    @PrimaryKey
    val recipeId: Long
)
