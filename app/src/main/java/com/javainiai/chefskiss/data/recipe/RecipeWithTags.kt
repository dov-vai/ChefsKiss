package com.javainiai.chefskiss.data.recipe

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.javainiai.chefskiss.data.tag.Tag

data class RecipeWithTags(
    @Embedded val recipe: Recipe,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            RecipeTagCrossRef::class,
            parentColumn = "recipeId",
            entityColumn = "tagId"
        )
    )
    val tags: List<Tag>
)
