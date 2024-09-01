package com.javainiai.chefskiss.data.database.services.recipeservice

import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.database.recipe.RecipeWithTags
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.enums.Sort
import kotlinx.coroutines.flow.Flow

interface RecipeService {
    fun getRecipeStream(id: Long): Flow<Recipe?>
    fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?>
    fun getRecipesWithIngredients(recipeIds: List<Long>): Flow<List<RecipeWithIngredients>>
    fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?>
    fun getAllTags(): Flow<List<Tag>>
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    suspend fun deleteRecipe(recipe: Recipe)
    suspend fun updateRecipe(recipe: Recipe)
    fun query(
        recipeName: String,
        rating: Int,
        isAsc: Boolean,
        sortingMethod: Sort,
        tags: List<Tag>,
        favorite: Boolean
    ): Flow<List<Recipe>>

    suspend fun updateRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    )

    suspend fun insertRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    )
}