package com.javainiai.chefskiss.data.database.planner

import kotlinx.coroutines.flow.Flow

class OfflinePlannerRepository(private val plannerDao: PlannerDao) : PlannerRepository {

    override fun getPlannerRecipesByDate(date: String): Flow<List<PlannerRecipe>> =
        plannerDao.getPlannerRecipesByDate(date)

    override fun getPlannerRecipesWithRecipes(date: String): Flow<List<PlannerRecipeWithRecipe>> =
        plannerDao.getPlannerRecipesWithRecipes(date)

    override fun getPlannerRecipesWithRecipes(dates: List<String>): Flow<List<PlannerRecipeWithRecipe>> =
        plannerDao.getPlannerRecipesWithRecipes(dates)

    override suspend fun insertPlannerRecipe(item: PlannerRecipe) = plannerDao.insert(item)
    override suspend fun updatePlannerRecipe(item: PlannerRecipe) = plannerDao.update(item)
    override suspend fun deletePlannerRecipe(item: PlannerRecipe) = plannerDao.delete(item)
}