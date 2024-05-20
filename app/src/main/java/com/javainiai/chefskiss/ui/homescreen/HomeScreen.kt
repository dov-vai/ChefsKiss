package com.javainiai.chefskiss.ui.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.components.search.RecipeCard
import com.javainiai.chefskiss.ui.components.search.SearchScreen
import com.javainiai.chefskiss.ui.components.search.SearchScreenViewModel
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import com.javainiai.chefskiss.utils.LocaleHelper
import kotlinx.coroutines.launch

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    drawerState: DrawerState,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    searchViewModel: SearchScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateTo: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val recipes by searchViewModel.recipes.collectAsState()
    val context = LocalContext.current

    var currentLanguage by remember { mutableStateOf("en") }

    SearchScreen(
        customButtonIcon = Icons.Default.Menu,
        onCustomButtonClick = { coroutineScope.launch { drawerState.open() } },
        bottomBar = {
            RecipeBottomBar(
                currentLanguage = currentLanguage,
                recipesOnClick = { },
                addOnClick = { navigateTo(AddRecipeDestination.route) },
                changeLanguageOnClick = {
                    val newLanguage = if (currentLanguage == "en") "lt" else "en"
                    currentLanguage = newLanguage
                    LocaleHelper.setLocale(context, newLanguage)
                }
            )
        },
        viewModel = searchViewModel
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable { navigateTo("${RecipeDetailsDestination.route}/${recipe.id}") }
                )
            }
        }
    }
}

@Composable
fun RecipeBottomBar(
    recipesOnClick: () -> Unit,
    addOnClick: () -> Unit,
    changeLanguageOnClick: () -> Unit,
    modifier: Modifier = Modifier,
    currentLanguage: String
) {
    BottomAppBar(modifier = modifier) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = recipesOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(imageVector = Icons.Default.RestaurantMenu, contentDescription = stringResource(R.string.recipes))
                }
                Text(text = stringResource(R.string.recipes))
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = addOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = stringResource(R.string.add_recipe))
                }
                Text(text = stringResource(R.string.add))
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = changeLanguageOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = if (currentLanguage == "en") "LT" else "EN")
                }
                Text(text = stringResource(R.string.language))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}