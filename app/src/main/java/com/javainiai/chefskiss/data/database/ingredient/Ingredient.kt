package com.javainiai.chefskiss.data.database.ingredient

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.enums.CookingUnit

@Entity(
    tableName = "ingredients",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val recipeId: Long,
    val name: String,
    val size: Float,
    val unit: CookingUnit
)