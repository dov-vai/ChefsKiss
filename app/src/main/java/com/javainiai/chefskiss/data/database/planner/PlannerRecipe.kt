package com.javainiai.chefskiss.data.database.planner

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.enums.Meal

@Entity(
    tableName = "planner_recipes",
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class PlannerRecipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val date: String, // format of yyyy-mm-dd
    @ColumnInfo(index = true)
    val recipeId: Long,
    val type: Meal
)
