package com.javainiai.chefskiss.data.recipe

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.javainiai.chefskiss.data.tag.Tag

@Entity(
    tableName = "recipes_tags",
    primaryKeys = ["recipeId", "tagId"],
    foreignKeys = [
        ForeignKey(
            entity = Recipe::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("recipeId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Tag::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("tagId"),
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class RecipeTagCrossRef(
    val recipeId: Long,
    @ColumnInfo(index = true)
    val tagId: Long
)
