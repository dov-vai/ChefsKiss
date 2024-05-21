package com.javainiai.chefskiss.data.tag

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeTagCrossRef

data class TagWithRecipes(
    @Embedded val tag: Tag,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            RecipeTagCrossRef::class,
            parentColumn = "tagId",
            entityColumn = "recipeId"
        )
    )
    val recipes: List<Recipe>
)
