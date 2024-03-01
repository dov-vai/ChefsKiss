package com.javainiai.chefskiss.data.ingredient

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val recipeId: Int,
    val name: String,
    val size: Int,
    val unit: String,
    val allergen: Boolean
)