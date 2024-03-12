package com.javainiai.chefskiss.data

import android.content.Context
import com.javainiai.chefskiss.data.recipe.OfflineRecipesRepository
import com.javainiai.chefskiss.data.recipe.RecipesRepository

interface AppContainer {
    val recipesRepository: RecipesRepository
}

class ChefsKissAppContainer(private val context: Context) : AppContainer {
    override val recipesRepository: RecipesRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineRecipesRepository(database.RecipeDao(), database.IngredientDao(), database.TagDao())
    }
}