package com.javainiai.chefskiss.ui.homescreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import kotlinx.coroutines.launch

object HomeScreenDestination : NavigationDestination {
    override val route = "home"
}

@Composable
fun HomeScreen(
    drawerState: DrawerState,
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateTo: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(modifier = modifier,
        topBar = { SearchTopBar(onMenuClick = { coroutineScope.launch { drawerState.open() } }) },
        bottomBar = {
            RecipeBottomBar(
                { /* TODO */ },
                { /* TODO */ },
                { /* TODO */ })
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, horizontalAlignment = Alignment.CenterHorizontally) {
            items(items = homeUiState.recipeList) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { navigateTo("${RecipeDetailsDestination.route}/${recipe.id}") })
            }
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = recipe.imagePath,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .size(64.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = recipe.title, maxLines = 1)
                Row {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = "Time to cook")
                    Text(
                        text = String.format(
                            "%02d:%02d",
                            recipe.cookingTime / 60,
                            recipe.cookingTime % 60
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(imageVector = Icons.Default.Star, contentDescription = "Rating")
                    Text(text = recipe.rating.toString())
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = if (recipe.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite recipe"
            )
        }
    }
}


@Composable
fun RecipeBottomBar(
    recentOnClick: () -> Unit,
    addOnClick: () -> Unit,
    favoritesOnClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomAppBar(modifier = modifier) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = recentOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Icon(imageVector = Icons.Default.History, contentDescription = "Recent recipes")
                }
                Text(text = "Recent")
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = addOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add recipe")
                }
                Text(text = "Add")
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = favoritesOnClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.background)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Favorite recipes"
                    )
                }
                Text(text = "Favorites")
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(onMenuClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            SearchBar(
                query = "",
                onQueryChange = {},
                onSearch = {},
                active = false,
                onActiveChange = {},
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Navigation drawer",
                        modifier = Modifier.clickable { onMenuClick() })
                },
                placeholder = { Text(text = "Search") }
            ) {}
        },
        modifier = Modifier.height(70.dp)
    )
}