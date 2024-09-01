package com.javainiai.chefskiss.data.database.services.plannerservice

import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.planner.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.database.planner.PlannerRepository
import kotlinx.coroutines.flow.Flow

class PlannerServiceImpl(private val plannerRepository: PlannerRepository) : PlannerService {
    override fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>> {
        return plannerRepository.getPlannerRecipesWithRecipes(date)
    }

    override fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>> {
        return plannerRepository.getPlannerRecipesWithRecipes(dates)
    }

    override suspend fun insertPlannerRecipe(item: PlannerRecipe) {
        plannerRepository.insertPlannerRecipe(item)
    }

    override suspend fun updatePlannerRecipe(item: PlannerRecipe) {
        plannerRepository.updatePlannerRecipe(item)
    }

    override suspend fun deletePlannerRecipe(item: PlannerRecipe) {
        plannerRepository.deletePlannerRecipe(item)
    }
}