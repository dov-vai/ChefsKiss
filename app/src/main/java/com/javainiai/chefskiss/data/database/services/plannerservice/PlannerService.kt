package com.javainiai.chefskiss.data.database.services.plannerservice

import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.planner.PlannerRecipeWithRecipe
import kotlinx.coroutines.flow.Flow

interface PlannerService {
    fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>>
    fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>>
    suspend fun insertPlannerRecipe(item: PlannerRecipe)
    suspend fun updatePlannerRecipe(item: PlannerRecipe)
    suspend fun deletePlannerRecipe(item: PlannerRecipe)
}