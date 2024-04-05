package com.javainiai.chefskiss.data.recipe

import androidx.room.Embedded
import androidx.room.Relation

data class PlannerRecipeWithRecipe(
    @Embedded
    val plannerRecipe: PlannerRecipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe
)