package com.javainiai.chefskiss.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.javainiai.chefskiss.ui.homescreen.HomeScreen
import com.javainiai.chefskiss.ui.homescreen.HomeScreenDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeScreen

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
            AddRecipeScreen(navigateBack = { navController.popBackStack() })
        }
    }

}