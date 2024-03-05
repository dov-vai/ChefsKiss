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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.javainiai.chefskiss.ui.navigation.ChefsKissNavHost

@Composable
fun ChefsKissApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        modifier = modifier,
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = { ChefsKissDrawerSheet() }) {
        ChefsKissNavHost(drawerState = drawerState, navController = navController)
    }
}


@Composable
fun ChefsKissDrawerSheet(modifier: Modifier = Modifier) {
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
            selected = true,
            onClick = { /*TODO*/ }
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
            selected = false,
            onClick = { /*TODO*/ }
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
            selected = false,
            onClick = { /*TODO*/ }
        )
    }
}