package com.javainiai.chefskiss.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.javainiai.chefskiss.ui.homescreen.HomeScreenDestination
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerDestination
import com.javainiai.chefskiss.ui.navigation.ChefsKissNavHost
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListDestination
import kotlinx.coroutines.launch

@Composable
fun ChefsKissApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentDestination by rememberSaveable { mutableStateOf(HomeScreenDestination.route) }
    val coroutineScope = rememberCoroutineScope()
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ChefsKissDrawerSheet(
                updateDestination = {
                    currentDestination = it
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                currentDestination = currentDestination
            )
        }
    ) {
        ChefsKissNavHost(
            drawerState = drawerState,
            currentDestination = currentDestination,
            navController = navController
        )
    }
}


@Composable
fun ChefsKissDrawerSheet(
    modifier: Modifier = Modifier,
    updateDestination: (String) -> Unit,
    currentDestination: String
) {
    ModalDrawerSheet(modifier = modifier) {
        Text(text = "Chef's Kiss", modifier = Modifier.padding(16.dp))
        NavigationDrawerItem(
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Home",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Home")
                }
            },
            selected = currentDestination == HomeScreenDestination.route,
            onClick = { updateDestination(HomeScreenDestination.route) }
        )
        NavigationDrawerItem(
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Restaurant,
                        contentDescription = "Meal Planner",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Meal Planner")
                }
            },
            selected = currentDestination == MealPlannerDestination.route,
            onClick = { updateDestination(MealPlannerDestination.route) }
        )
        NavigationDrawerItem(
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.ShoppingBasket,
                        contentDescription = "Shopping List",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Shopping List")
                }
            },
            selected = currentDestination == ShoppingListDestination.route,
            onClick = { updateDestination(ShoppingListDestination.route) }
        )
    }
}