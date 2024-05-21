package com.javainiai.chefskiss.data.recipe

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

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
