package com.javainiai.chefskiss.ui.recipescreen

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.ShoppingBasket
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.enums.Meal
import com.javainiai.chefskiss.data.enums.UnitSystem
import com.javainiai.chefskiss.data.pdf.exportAsPdf
import com.javainiai.chefskiss.data.pdf.getRecipeCardHtml
import com.javainiai.chefskiss.data.utils.CalendarUtils
import com.javainiai.chefskiss.data.utils.UnitUtils.convertUnit
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.components.ConfirmationDialog
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.timer.TimerDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

object RecipeDetailsDestination : NavigationDestination {
    override val route = "recipe_display"
    const val recipeIdArg = "recipeId"
    val routeWithArgs = "$route/{$recipeIdArg}"
}

@Composable
fun RecipeDetailsScreen(
    navigateBack: () -> Unit,
    navigateTo: (String) -> Unit,
    viewModel: RecipeDetailsViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val checkedIngredients by viewModel.checkedIngredients.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val customServingSize by viewModel.customServingSize.collectAsState()
    val unitSystem by viewModel.unitSystem.collectAsState()

    Scaffold(
        topBar = {
            RecipeTopBar(
                onTimer = { navigateTo("${TimerDestination.route}/${uiState.recipe.id}") },
                isFavorite = uiState.recipe.favorite,
                onFavorite = viewModel::updateFavorite,
                onEdit = { navigateTo("${EditRecipeDestination.route}/${uiState.recipe.id}") },
                onShopping = viewModel::addToShoppingList,
                onBack = navigateBack,
                onDelete = {
                    coroutineScope.launch {
                        viewModel.deleteRecipe()
                        navigateBack()
                    }
                },
                onAddToMealPlanner = { date, type ->
                    viewModel.addToMealPlanner(
                        PlannerRecipe(
                            date = date,
                            recipeId = uiState.recipe.id,
                            type = type
                        )
                    )
                },
                onExportPdf = {
                    coroutineScope.launch {
                        val webView = WebView(context).apply {
                            webViewClient = WebViewClient()
                            loadDataWithBaseURL(
                                null,
                                getRecipeCardHtml(
                                    recipe = uiState.recipe,
                                    ingredients = uiState.ingredients
                                ),
                                "text/html",
                                "UTF-8",
                                null
                            )
                        }
                        exportAsPdf(
                            webView = webView,
                            context = context,
                            documentName = uiState.recipe.title
                        )
                    }
                }
            )
        },
        bottomBar = { RecipeBottomBar(viewModel.screenIndex, viewModel::updateScreenIndex) },
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) }
    ) { padding ->
        when (viewModel.screenIndex) {
            0 -> RecipeAbout(
                recipe = uiState.recipe,
                tags = tags,
                modifier = Modifier.padding(padding),
                onRating = viewModel::updateRating,
                servingSize = uiState.recipe.servings
            )

            1 -> RecipeIngredients(
                ingredients = uiState.ingredients,
                checkedIngredients = checkedIngredients,
                updateChecked = viewModel::updateCheckedIngredients,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxWidth(),
                multiplier = customServingSize.toFloat() / uiState.recipe.servings,
                unitSystem = unitSystem,
                onUnitSystemChange = viewModel::updateUnitSystem,
                onCustomServingSizeUpdate = viewModel::adjustServingSize,
                customServingSize = customServingSize
            )

            2 -> RecipeInstructions(recipe = uiState.recipe, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
fun RecipeAbout(
    recipe: Recipe,
    tags: List<Tag>,
    modifier: Modifier = Modifier,
    onRating: (Int) -> Unit,
    servingSize: Int
) {
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
                    Icon(imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.clickable { onRating(it + 1) })
                }
                repeat(5 - recipe.rating) {
                    Icon(imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.clickable { onRating(recipe.rating + it + 1) })
                }
            }
            HorizontalDivider(modifier = Modifier.padding(10.dp))
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
                    contentDescription = stringResource(R.string.title),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = recipe.title, fontWeight = FontWeight.Bold)
            }
            Row {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = stringResource(R.string.cookingTime),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = stringResource(R.string.cookingTime))
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        recipe.cookingTime / 60,
                        recipe.cookingTime % 60
                    )
                )
            }
            Row {
                Icon(
                    imageVector = Icons.Default.People,
                    contentDescription = stringResource(R.string.cookingTime),
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = stringResource(R.string.servingSize))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = servingSize.toString())
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    checked: Boolean,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    multiplier: Float
) {
    val context = LocalContext.current

    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = containerColor)) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = ingredient.name,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (ingredient.size == 0f) "" else String.format(
                    Locale.getDefault(),
                    "%.2f",
                    ingredient.size * multiplier
                )
            )
            Text(
                text = ingredient.unit.getTitle(context),
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Checkbox(checked = checked, onCheckedChange = { onCheckedChange(it) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeIngredients(
    ingredients: List<Ingredient>,
    checkedIngredients: List<Ingredient>,
    updateChecked: (List<Ingredient>) -> Unit,
    modifier: Modifier = Modifier,
    multiplier: Float,
    unitSystem: UnitSystem,
    onUnitSystemChange: (UnitSystem) -> Unit,
    customServingSize: Int,
    onCustomServingSizeUpdate: (Int) -> Unit
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(
                selected = unitSystem == UnitSystem.Metric,
                onClick = { onUnitSystemChange(UnitSystem.Metric) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
            )
            {
                Text(text = stringResource(R.string.metric))
            }
            SegmentedButton(
                selected = unitSystem == UnitSystem.Imperial,
                onClick = { onUnitSystemChange(UnitSystem.Imperial) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
            )
            {
                Text(text = stringResource(R.string.imperial))
            }
        }

        Card(
            modifier = Modifier.padding(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                Text(text = stringResource(R.string.adjustServingSize))
                Spacer(modifier = Modifier.weight(1f))
                Text(text = customServingSize.toString())
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onCustomServingSizeUpdate(customServingSize - 1) }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(R.string.remove1FromServingSize)
                    )
                }
                IconButton(onClick = { onCustomServingSizeUpdate(customServingSize + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.add1ToServingSize)
                    )
                }
            }
        }

        LazyColumn {
            items(ingredients) { ingredient ->
                IngredientCard(
                    ingredient = ingredient.convertUnit(unitSystem),
                    checked = checkedIngredients.contains(ingredient),
                    onCheckedChange = {
                        if (it) {
                            updateChecked(checkedIngredients + ingredient)
                        } else {
                            updateChecked(checkedIngredients - ingredient)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    multiplier = multiplier
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
        RecipeScreen(stringResource(R.string.about), Icons.Default.RestaurantMenu),
        RecipeScreen(stringResource(R.string.ingredients), Icons.Default.ShoppingBasket),
        RecipeScreen(stringResource(R.string.directions), Icons.AutoMirrored.Default.MenuBook)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MealPlannerDialog(
    onDismiss: () -> Unit,
    onAddToMealPlanner: (String, Meal) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedType by remember { mutableStateOf(Meal.BREAKFAST) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = stringResource(R.string.quickAddToMealPlanner))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Meal.entries.forEach {
                        FilterChip(
                            selected = selectedType == it,
                            onClick = { selectedType = it },
                            label = { Text(text = it.getTitle(context)) })
                    }
                }
                HorizontalDivider()
                val startOfWeek = CalendarUtils.getStartOfWeek()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val weekdayFormat = SimpleDateFormat("EEEE, MM-dd", Locale.getDefault())
                repeat(7) {
                    val date = CalendarUtils.datePlusOffset(startOfWeek, it)
                    val dateString = dateFormat.format(date)
                    val weekdayString = weekdayFormat.format(date)
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                        Text(
                            text = weekdayString,
                            modifier = Modifier
                                .clickable {
                                    onAddToMealPlanner(
                                        dateString,
                                        selectedType
                                    )
                                    onDismiss()
                                }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeTopBar(
    onTimer: () -> Unit,
    isFavorite: Boolean,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onShopping: () -> Unit,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onAddToMealPlanner: (String, Meal) -> Unit,
    onExportPdf: () -> Unit,
) {
    var showDialog by remember {
        mutableStateOf(false)
    }

    var showMealPlannerDialog by remember {
        mutableStateOf(false)
    }

    var expanded by remember {
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
            text = { Text(stringResource(R.string.deleteConfirmation)) })
    }

    if (showMealPlannerDialog) {
        MealPlannerDialog(
            onDismiss = { showMealPlannerDialog = false },
            onAddToMealPlanner = onAddToMealPlanner
        )
    }

    TopAppBar(
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(R.string.back)
                )
            }
        },
        title = {
            Row {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onTimer) {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = stringResource(R.string.timer)
                    )
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite)
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete)
                    )
                }
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopEnd)
                ) {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.more)
                        )
                    }
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = stringResource(R.string.addToShoppingList)
                                )
                            },
                            text = { Text(text = stringResource(R.string.addToShoppingList)) },
                            onClick = {
                                expanded = false
                                onShopping()
                            })
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = stringResource(R.string.addToMealPlanner)
                                )
                            },
                            text = { Text(text = stringResource(R.string.addToMealPlanner)) },
                            onClick = {
                                expanded = false
                                showMealPlannerDialog = true
                            })
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = stringResource(R.string.printExportasPDF)
                                )
                            },
                            text = { Text(text = stringResource(R.string.printExportasPDF)) },
                            onClick = {
                                expanded = false
                                onExportPdf()
                            })
                    }
                }
            }
        })
}