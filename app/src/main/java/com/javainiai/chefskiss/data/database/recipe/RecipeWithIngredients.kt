package com.javainiai.chefskiss.data.database.recipe

import androidx.room.Embedded
import androidx.room.Relation
import com.javainiai.chefskiss.data.database.ingredient.Ingredient

data class RecipeWithIngredients(
    @Embedded
    val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "recipeId"
    )
    val ingredients: List<Ingredient>
)