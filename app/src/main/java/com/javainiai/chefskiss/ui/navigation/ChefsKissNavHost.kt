package com.javainiai.chefskiss.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.javainiai.chefskiss.ui.mealplanner.PlannerEditDestination
import com.javainiai.chefskiss.ui.mealplanner.PlannerEditScreen
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeScreen
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsScreen
import com.javainiai.chefskiss.ui.selectionscreen.SelectionDestination
import com.javainiai.chefskiss.ui.selectionscreen.SelectionScreen

@Composable
fun ChefsKissNavHost(
    drawerState: DrawerState,
    currentDestination: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavDrawerNavigatorDestination.route,
        modifier = modifier
    ) {
        composable(route = NavDrawerNavigatorDestination.route) {
            NavDrawerNavigator(
                drawerState = drawerState,
                currentDestination = currentDestination,
                navigateBack = { navController.navigateUp() }) {
                navController.navigate(it)
            }
        }
        composable(route = AddRecipeDestination.route) {
            AddRecipeScreen(navigateBack = { navController.navigateUp() })
        }
        composable(
            route = RecipeDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RecipeDetailsDestination.recipeIdArg) {
                type = NavType.LongType
            })
        ) {
            RecipeDetailsScreen(navigateBack = { navController.navigateUp() })
        }
        composable(route = SelectionDestination.route) {
            SelectionScreen(navigateBack = { navController.navigateUp() })
        }
        composable(
            route = PlannerEditDestination.routeWithArgs,
            arguments = listOf(navArgument(PlannerEditDestination.plannerDateArg) {
                type = NavType.StringType
            })
        ) {
            PlannerEditScreen(
                navigateBack = { navController.navigateUp() },
                navigateToSelection = { navController.navigate(SelectionDestination.route) })
        }
    }

}