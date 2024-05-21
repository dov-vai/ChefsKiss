package com.javainiai.chefskiss.data.recipe

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class Recipe(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String,
    val cookingTime: Int,
    val servings: Int,
    val rating: Int,
    val favorite: Boolean,
    val imagePath: Uri
)
