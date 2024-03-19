package com.javainiai.chefskiss.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.javainiai.chefskiss.ui.homescreen.HomeScreen
import com.javainiai.chefskiss.ui.homescreen.HomeScreenDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeScreen
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsScreen
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingList
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListDestination

@Composable
fun ChefsKissNavHost(
    drawerState: DrawerState,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeScreenDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeScreenDestination.route) {
            HomeScreen(drawerState) {
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
        composable(route = ShoppingListDestination.route) {
            ShoppingList(navigateBack = { navController.navigateUp() })
        }
    }

}