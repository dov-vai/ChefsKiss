package com.javainiai.chefskiss.data.recipe

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.javainiai.chefskiss.data.ingredient.Ingredient

@Entity(
    tableName = "shopping_checked_ingredients",
    primaryKeys = ["ingredientId", "recipeId"],
    foreignKeys = [
        ForeignKey(
            entity = ShopRecipe::class,
            parentColumns = arrayOf("recipeId"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("ingredientId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class ShopIngredient(
    val ingredientId: Long,
    @ColumnInfo(index = true)
    val recipeId: Long
)