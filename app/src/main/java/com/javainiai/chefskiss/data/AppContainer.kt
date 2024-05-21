package com.javainiai.chefskiss.data

import android.content.Context
import com.javainiai.chefskiss.data.config.ConfigRepository
import com.javainiai.chefskiss.data.config.OfflineConfigRepository
import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource
import com.javainiai.chefskiss.data.recipe.OfflineRecipesRepository
import com.javainiai.chefskiss.data.recipe.RecipesRepository

interface AppContainer {
    val recipesRepository: RecipesRepository
    val selectedRecipeDataSource: SelectedRecipeDataSource
    val configRepository: ConfigRepository
}

class ChefsKissAppContainer(val context: Context) : AppContainer {
    override val recipesRepository: RecipesRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineRecipesRepository(database.RecipeDao(), database.IngredientDao(), database.TagDao())
    }

    override val selectedRecipeDataSource: SelectedRecipeDataSource by lazy {
        SelectedRecipeDataSource()
    }

    override val configRepository: ConfigRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineConfigRepository(database.ConfigDao())
    }
}