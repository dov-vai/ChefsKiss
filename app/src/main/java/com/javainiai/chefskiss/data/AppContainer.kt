package com.javainiai.chefskiss.data

import android.content.Context
import com.javainiai.chefskiss.data.database.RecipeDatabase
import com.javainiai.chefskiss.data.database.config.ConfigRepository
import com.javainiai.chefskiss.data.database.config.OfflineConfigRepository
import com.javainiai.chefskiss.data.database.ingredient.IngredientsRepository
import com.javainiai.chefskiss.data.database.ingredient.OfflineIngredientsRepository
import com.javainiai.chefskiss.data.database.planner.OfflinePlannerRepository
import com.javainiai.chefskiss.data.database.planner.PlannerRepository
import com.javainiai.chefskiss.data.database.recipe.OfflineRecipesRepository
import com.javainiai.chefskiss.data.database.recipe.RecipesRepository
import com.javainiai.chefskiss.data.database.services.configservice.ConfigService
import com.javainiai.chefskiss.data.database.services.configservice.ConfigServiceImpl
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerService
import com.javainiai.chefskiss.data.database.services.plannerservice.PlannerServiceImpl
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeService
import com.javainiai.chefskiss.data.database.services.recipeservice.RecipeServiceImpl
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingService
import com.javainiai.chefskiss.data.database.services.shoppingservice.ShoppingServiceImpl
import com.javainiai.chefskiss.data.database.shoppinglist.OfflineShoppingListRepository
import com.javainiai.chefskiss.data.database.shoppinglist.ShoppingListRepository
import com.javainiai.chefskiss.data.database.tag.OfflineTagsRepository
import com.javainiai.chefskiss.data.database.tag.TagsRepository
import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource

interface AppContainer {
    val recipeService: RecipeService
    val shoppingListService: ShoppingService
    val plannerService: PlannerService
    val selectedRecipeDataSource: SelectedRecipeDataSource
    val configService: ConfigService
}

class ChefsKissAppContainer(val context: Context) : AppContainer {
    private val recipesRepository: RecipesRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineRecipesRepository(database.RecipeDao())
    }

    private val ingredientsRepository: IngredientsRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineIngredientsRepository(database.IngredientDao())
    }

    private val tagsRepository: TagsRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineTagsRepository(database.TagDao())
    }

    private val plannerRepository: PlannerRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflinePlannerRepository(database.PlannerDao())
    }

    private val shoppingListRepository: ShoppingListRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineShoppingListRepository(database.ShoppingListDao())
    }

    private val configRepository: ConfigRepository by lazy {
        val database = RecipeDatabase.getDatabase(context)
        OfflineConfigRepository(database.ConfigDao())
    }

    override val recipeService: RecipeService by lazy {
        RecipeServiceImpl(recipesRepository, ingredientsRepository, tagsRepository)
    }

    override val plannerService: PlannerService by lazy {
        PlannerServiceImpl(plannerRepository)
    }

    override val shoppingListService: ShoppingService by lazy {
        ShoppingServiceImpl(shoppingListRepository)
    }

    override val configService: ConfigService by lazy {
        ConfigServiceImpl(configRepository)
    }

    override val selectedRecipeDataSource: SelectedRecipeDataSource by lazy {
        SelectedRecipeDataSource()
    }
}