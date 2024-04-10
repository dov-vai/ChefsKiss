package com.javainiai.chefskiss.ui.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.ShopIngredient
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.recipescreen.IngredientCard
import kotlinx.coroutines.launch

object ShoppingListDestination : NavigationDestination {
    override val route = "shopping"
}

@Composable
fun ShoppingList(
    drawerState: DrawerState,
    viewModel: ShoppingListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val checkedIngredients by viewModel.checkedIngredients.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = { ShoppingListTopBar(onMenuClick = { coroutineScope.launch { drawerState.open() } }) },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(uiState.recipesWithIngredients) {
                RecipeCard(
                    recipe = it.recipe,
                    ingredients = it.ingredients,
                    onRemove = { viewModel.removeRecipe(it.recipe) },
                    checkedIngredients = checkedIngredients,
                    removeCheckedIngredient = viewModel::removeCheckedIngredient,
                    addCheckedIngredient = viewModel::addCheckedIngredient,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

}


@Composable
fun RecipeCard(
    recipe: Recipe,
    ingredients: List<Ingredient>,
    checkedIngredients: List<ShopIngredient>,
    removeCheckedIngredient: (ShopIngredient) -> Unit,
    addCheckedIngredient: (ShopIngredient) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var opened by remember {
        mutableStateOf(false)
    }
    ElevatedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AsyncImage(
                    model = recipe.imagePath,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = recipe.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
                IconButton(onClick = { opened = !opened }) {
                    Icon(
                        imageVector = if (opened) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Open recipe ingredients"
                    )
                }
            }
            if (opened) {
                Column {
                    ingredients.forEach { ingredient ->
                        val shopIngredient = ShopIngredient(ingredient.id, ingredient.recipeId)
                        IngredientCard(ingredient = ingredient,
                            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
                            checked = checkedIngredients.contains(shopIngredient),
                            onCheckedChange = {
                                if (it) {
                                    addCheckedIngredient(shopIngredient)
                                } else {
                                    removeCheckedIngredient(shopIngredient)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListTopBar(onMenuClick: () -> Unit, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = "Shopping List")
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open navigation drawer"
                )
            }
        },
    )
}