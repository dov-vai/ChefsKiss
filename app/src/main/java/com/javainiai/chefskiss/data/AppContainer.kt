package com.javainiai.chefskiss.data

import android.content.Context
import com.javainiai.chefskiss.data.ingredient.IngredientsRepository
import com.javainiai.chefskiss.data.ingredient.OfflineIngredientsRepository
import com.javainiai.chefskiss.data.recipe.OfflineRecipesRepository
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.OfflineTagsRepository
import com.javainiai.chefskiss.data.tag.TagsRepository

interface AppContainer {
    val recipesRepository: RecipesRepository
    val ingredientsRepository: IngredientsRepository
    val tagsRepository: TagsRepository
}

class ChefsKissAppContainer(private val context: Context) : AppContainer {
    override val recipesRepository: RecipesRepository by lazy {
        OfflineRecipesRepository(RecipeDatabase.getDatabase(context).RecipeDao())
    }
    override val ingredientsRepository: IngredientsRepository by lazy {
        OfflineIngredientsRepository(RecipeDatabase.getDatabase(context).IngredientDao())
    }
    override val tagsRepository: TagsRepository by lazy {
        OfflineTagsRepository(RecipeDatabase.getDatabase(context).TagDao())
    }
}