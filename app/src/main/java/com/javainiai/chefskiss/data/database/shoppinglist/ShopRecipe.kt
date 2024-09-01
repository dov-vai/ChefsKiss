package com.javainiai.chefskiss.data.database.shoppinglist

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.javainiai.chefskiss.data.database.recipe.Recipe

@Entity(
    tableName = "shopping_list",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ShopRecipe(
    @PrimaryKey
    val recipeId: Long
)
