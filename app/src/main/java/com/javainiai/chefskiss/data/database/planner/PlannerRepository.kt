package com.javainiai.chefskiss.data.database.planner

import kotlinx.coroutines.flow.Flow

interface PlannerRepository {
    fun getPlannerRecipesByDate(date: String): Flow<List<PlannerRecipe>>
    fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>>
    fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>>
    suspend fun insertPlannerRecipe(item: PlannerRecipe)
    suspend fun updatePlannerRecipe(item: PlannerRecipe)
    suspend fun deletePlannerRecipe(item: PlannerRecipe)
}