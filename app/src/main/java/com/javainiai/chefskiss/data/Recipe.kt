package com.javainiai.chefskiss.data

import android.net.Uri

data class Recipe(
    val title: String,
    val description: String,
    val cookingTime: Int,
    val rating: Int,
    val favorite: Boolean,
    val imagePath: Uri
)
