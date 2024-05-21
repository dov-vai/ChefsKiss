package com.javainiai.chefskiss.ui.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.enums.Language
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.homescreen.HomeScreenDestination
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerDestination
import com.javainiai.chefskiss.ui.navigation.ChefsKissNavHost
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListDestination
import kotlinx.coroutines.launch

@Composable
fun ChefsKissApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: ChefsKissAppViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var currentDestination by rememberSaveable { mutableStateOf(HomeScreenDestination.route) }
    val coroutineScope = rememberCoroutineScope()
    val language by viewModel.languageState.collectAsState()

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
                currentDestination = currentDestination,
                currentLanguage = language,
                onLanguageClick = viewModel::updateLanguage
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefsKissDrawerSheet(
    modifier: Modifier = Modifier,
    updateDestination: (String) -> Unit,
    currentDestination: String,
    currentLanguage: Language,
    onLanguageClick: (Language) -> Unit,
) {
    ModalDrawerSheet(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Chef's Kiss", modifier = Modifier.padding(16.dp))
        }

        NavigationDrawerItem(
            label = {
                Row {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = stringResource(R.string.home),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.home))
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
                        contentDescription = stringResource(R.string.mealPlanner),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.mealPlanner))
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
                        contentDescription = stringResource(R.string.shoppingList),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.shoppingList))
                }
            },
            selected = currentDestination == ShoppingListDestination.route,
            onClick = { updateDestination(ShoppingListDestination.route) }
        )
        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SingleChoiceSegmentedButtonRow {
                val count = Language.entries.count()
                Language.entries.forEachIndexed { index, lang ->
                    SegmentedButton(
                        selected = currentLanguage == lang,
                        onClick = { onLanguageClick(lang) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = count)
                    ) {
                        Text(text = lang.title)
                    }
                }
            }
        }
    }
}