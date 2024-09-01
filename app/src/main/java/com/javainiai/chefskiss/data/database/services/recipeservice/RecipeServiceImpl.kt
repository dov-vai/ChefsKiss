package com.javainiai.chefskiss.data.database.services.recipeservice

import androidx.sqlite.db.SimpleSQLiteQuery
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.ingredient.IngredientsRepository
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.recipe.RecipeTagCrossRef
import com.javainiai.chefskiss.data.database.recipe.RecipeWithIngredients
import com.javainiai.chefskiss.data.database.recipe.RecipeWithTags
import com.javainiai.chefskiss.data.database.recipe.RecipesRepository
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.database.tag.TagsRepository
import com.javainiai.chefskiss.data.enums.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class RecipeServiceImpl(
    private val recipeRepository: RecipesRepository,
    private val ingredientRepository: IngredientsRepository,
    private val tagRepository: TagsRepository
) : RecipeService {
    override suspend fun insertRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    ) {
        val recipeId = recipeRepository.insertRecipe(recipe)

        for (ingredient in ingredients) {
            ingredientRepository.insertIngredient(ingredient.copy(recipeId = recipeId))
        }

        for (tag in tags) {
            recipeRepository.insertRecipeTag(RecipeTagCrossRef(recipeId, tag.id))
        }
    }

    override suspend fun updateRecipeWithIngredientsAndTags(
        recipe: Recipe,
        ingredients: List<Ingredient>,
        tags: List<Tag>
    ) {
        recipeRepository.updateRecipe(recipe)

        // have to be cleared before updating
        val ingredientsToRemove =
            recipeRepository.getRecipeWithIngredients(recipe.id).first()!!.ingredients
        val tagsToRemove = recipeRepository.getRecipeWithTags(recipe.id).first()!!.tags

        for (ingredient in ingredientsToRemove) {
            ingredientRepository.deleteIngredient(ingredient)
        }
        for (tag in tagsToRemove) {
            recipeRepository.deleteRecipeTag(RecipeTagCrossRef(recipe.id, tag.id))
        }

        for (ingredient in ingredients) {
            ingredientRepository.insertIngredient(ingredient)
        }
        for (tag in tags) {
            recipeRepository.insertRecipeTag(RecipeTagCrossRef(recipe.id, tag.id))
        }
    }

    override fun getRecipeStream(id: Long): Flow<Recipe?> {
        return recipeRepository.getRecipeStream(id)
    }

    override fun getRecipeWithIngredients(id: Long): Flow<RecipeWithIngredients?> {
        return recipeRepository.getRecipeWithIngredients(id)
    }

    override fun getRecipesWithIngredients(recipeIds: List<Long>): Flow<List<RecipeWithIngredients>> {
        return recipeRepository.getRecipesWithIngredients(recipeIds)
    }

    override fun getRecipeWithTags(id: Long): Flow<RecipeWithTags?> {
        return recipeRepository.getRecipeWithTags(id)
    }

    override fun getAllTags(): Flow<List<Tag>> {
        return tagRepository.getAllTagsStream()
    }

    override suspend fun insertTag(tag: Tag): Long {
        return tagRepository.insertTag(tag)
    }

    override suspend fun deleteTag(tag: Tag) {
        tagRepository.deleteTag(tag)
    }

    override suspend fun deleteRecipe(recipe: Recipe) {
        recipeRepository.deleteRecipe(recipe)
    }

    override suspend fun updateRecipe(recipe: Recipe) {
        recipeRepository.updateRecipe(recipe)
    }

    override fun query(
        recipeName: String,
        rating: Int,
        isAsc: Boolean,
        sortingMethod: Sort,
        tags: List<Tag>,
        favorite: Boolean
    ): Flow<List<Recipe>> {

        var query = "SELECT * FROM recipes WHERE rating >= ? AND title LIKE ?"

        if (favorite) {
            query += " AND favorite = true"
        }

        if (tags.isNotEmpty()) {
            val tagIds = tags.map { it.id }.joinToString()
            query += " AND id IN (SELECT recipeId from recipes_tags WHERE tagId IN ($tagIds))"
        }

        val sort = when (sortingMethod) {
            Sort.COOKING_TIME -> "cookingTime"
            Sort.PORTION -> "servings"
            Sort.RATING -> "rating"
            Sort.ADDED -> "id"
        }

        val order = if (isAsc) "ASC" else "DESC"

        query += " ORDER BY $sort $order"

        return recipeRepository.query(SimpleSQLiteQuery(query, arrayOf(rating, "%$recipeName%")))
    }


}