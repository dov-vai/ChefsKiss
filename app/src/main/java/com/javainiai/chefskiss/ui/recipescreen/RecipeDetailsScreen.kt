package com.javainiai.chefskiss.ui.recipescreen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import kotlinx.coroutines.launch

object RecipeDetailsDestination : NavigationDestination {
    override val route = "recipe_display"
    const val recipeIdArg = "recipeId"
    val routeWithArgs = "$route/{$recipeIdArg}"
}

@Composable
fun RecipeDetailsScreen(
    navigateBack: () -> Unit,
    viewModel: RecipeDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            RecipeTopBar(
                isFavorite = uiState.recipe.favorite,
                onFavorite = viewModel::updateFavorite,
                onShopping = viewModel::addToShoppingList,
                onBack = navigateBack,
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deleteRecipe()
                        navigateBack()
                    }
                }
            )
        },
        bottomBar = { RecipeBottomBar(viewModel.screenIndex, viewModel::updateScreenIndex) }
    ) { padding ->
        when (viewModel.screenIndex) {
            0 -> RecipeAbout(
                recipe = uiState.recipe,
                tags = tags,
                modifier = Modifier.padding(padding)
            )

            1 -> RecipeIngredients(
                ingredients = uiState.ingredients,
                modifier = Modifier.padding(padding)
            )

            2 -> RecipeInstructions(recipe = uiState.recipe, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun RecipeAbout(recipe: Recipe, tags: List<Tag>, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = recipe.imagePath,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(256.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .align(alignment = Alignment.CenterHorizontally)
            )
            Row {
                Spacer(modifier = Modifier.weight(1f))
                repeat(recipe.rating) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null)
                }
                repeat(5 - recipe.rating) {
                    Icon(imageVector = Icons.Default.StarBorder, contentDescription = null)
                }
            }
            Divider(modifier = Modifier.padding(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tags.forEach {
                    OutlinedCard {
                        Text(text = it.title, modifier = Modifier.padding(8.dp))
                    }
                }
            }
            Row {
                Icon(
                    imageVector = Icons.Default.RestaurantMenu,
                    contentDescription = "Title",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = recipe.title, fontWeight = FontWeight.Bold)
            }
            Row {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = "Cooking time",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Cooking time")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = String.format(
                        "%02d:%02d",
                        recipe.cookingTime / 60,
                        recipe.cookingTime % 60
                    )
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = "Cooking time",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = "Serving size")
                Spacer(modifier = Modifier.weight(1f))
                Text(text = recipe.servings.toString())
            }
        }
    }
}

@Composable
fun IngredientCard(ingredient: Ingredient, modifier: Modifier = Modifier) {
    var checked by remember {
        mutableStateOf(false)
    }

    Card(modifier = modifier) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "${ingredient.name} ${if (ingredient.size == 0f) "" else ingredient.size} ${ingredient.unit}",
                fontSize = 20.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Checkbox(checked = checked, onCheckedChange = { checked = !checked })
        }
    }
}

@Composable
fun RecipeIngredients(ingredients: List<Ingredient>, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        LazyColumn {
            items(ingredients) {
                IngredientCard(
                    ingredient = it, modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun RecipeInstructions(recipe: Recipe, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Text(
            text = recipe.description,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            fontSize = 24.sp
        )
    }
}


data class RecipeScreen(
    val title: String,
    val icon: ImageVector
)

@Composable
fun RecipeBottomBar(screenIndex: Int, updateScreen: (Int) -> Unit, modifier: Modifier = Modifier) {
    val screens = listOf(
        RecipeScreen("About", Icons.Default.RestaurantMenu),
        RecipeScreen("Ingredients", Icons.Default.ShoppingBasket),
        RecipeScreen("Directions", Icons.AutoMirrored.Default.MenuBook)
    )
    BottomAppBar(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            screens.forEachIndexed { i, screen ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    FilledTonalButton(
                        onClick = { updateScreen(i) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor =
                            if (screenIndex == i) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.background
                        )
                    ) {
                        Icon(imageVector = screen.icon, contentDescription = screen.title)
                    }
                    Text(text = screen.title)
                }
            }
        }
    }
}

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onDismissRequest: () -> Unit,
    text: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = onConfirm) {
            Text(text = "Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text(text = "Dismiss")
        }
    },
        text = text
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeTopBar(
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onShopping: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    if (showDialog) {
        ConfirmationDialog(
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                onDelete()
            },
            onDismissRequest = { showDialog = false },
            text = { Text("Are you sure that you want to delete this recipe? (Can't be recovered)") })
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        title = {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Timer, contentDescription = "Timer")
                }
                IconButton(onClick = onShopping) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Add to shopping list"
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        })
}