package com.javainiai.chefskiss.ui.navigation

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import com.javainiai.chefskiss.ui.homescreen.HomeScreen
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerDestination
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerScreen
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingList
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListDestination

object NavDrawerNavigatorDestination : NavigationDestination {
    override val route = "navDrawerNavigator"
}

@Composable
fun NavDrawerNavigator(
    drawerState: DrawerState,
    currentDestination: String,
    navigateTo: (String) -> Unit
) {
    when (currentDestination) {
        MealPlannerDestination.route -> MealPlannerScreen(drawerState = drawerState) {
            navigateTo(it)
        }

        ShoppingListDestination.route -> ShoppingList(drawerState = drawerState)
        else -> HomeScreen(drawerState = drawerState) {
            navigateTo(it)
        }
    }
}