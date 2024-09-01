package com.javainiai.chefskiss.data.database.planner

import androidx.room.Embedded
import androidx.room.Relation
import com.javainiai.chefskiss.data.database.recipe.Recipe

data class PlannerRecipeWithRecipe(
    @Embedded
    val plannerRecipe: PlannerRecipe,
    @Relation(
        parentColumn = "recipeId",
        entityColumn = "id"
    )
    val recipe: Recipe
)