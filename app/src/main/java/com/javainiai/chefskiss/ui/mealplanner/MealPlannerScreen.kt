package com.javainiai.chefskiss.ui.mealplanner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.utils.CalendarUtils
import com.javainiai.chefskiss.data.utils.CalendarUtils.getDateString
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object MealPlannerDestination : NavigationDestination {
    override val route = "mealplanner"
}

@Composable
fun MealPlannerScreen(
    drawerState: DrawerState,
    viewModel: MealPlannerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateTo: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val plannerRecipes by viewModel.plannerRecipes.collectAsState()
    val undoVisible =
        uiState.startOfWeek.getDateString() != CalendarUtils.getStartOfWeek().getDateString()

    DisposableEffect(Unit) {
        onDispose {
            viewModel.messageInProgress?.cancel()
        }
    }

    if (uiState.bulkEditMode) {
        MealPlannerBulkEditScreen(
            startOfWeek = uiState.startOfWeek,
            topBarTitle = uiState.title,
            plannerRecipes = plannerRecipes,
            selectedRecipes = uiState.selectedRecipes,
            updateSelectedRecipes = viewModel::updateSelectedRecipes,
            onNavigateBack = {
                viewModel.updateBulkEditMode(false)
                viewModel.updateSelectedRecipes(listOf())
                viewModel.updateStartOfWeek(uiState.bulkEditWeek)
            },
            onBack = viewModel::shiftBackwards,
            onForward = viewModel::shiftForward,
            onDone = { viewModel.updateStartOfWeek(uiState.bulkEditWeek) },
            pasteMeals = viewModel::copyMeals,
            moveMeals = viewModel::moveMeals,
            deleteMeals = viewModel::deleteMeals
        )
    } else {
        MealPlannerBrowseScreen(
            navigateTo = navigateTo,
            topBarTitle = uiState.title,
            onBack = viewModel::shiftBackwards,
            onForward = viewModel::shiftForward,
            onUndo = viewModel::revertStartOfWeek,
            undoVisible = undoVisible,
            drawerState = drawerState,
            onBulkEditClick = {
                viewModel.updateBulkEditMode(true)
                viewModel.updateBulkEditWeek(uiState.startOfWeek)
            },
            snackbarHostState = viewModel.snackbarHostState,
            startOfWeek = uiState.startOfWeek,
            currentDate = uiState.currentDate,
            plannerRecipes = plannerRecipes,
            addToShoppingList = viewModel::addToShoppingList
        )
    }
}

@Composable
fun MealPlannerBrowseScreen(
    modifier: Modifier = Modifier,
    navigateTo: (String) -> Unit,
    topBarTitle: String,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onUndo: () -> Unit,
    undoVisible: Boolean,
    drawerState: DrawerState,
    onBulkEditClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
    startOfWeek: Date,
    currentDate: Date,
    plannerRecipes: Map<String, List<PlannerRecipeWithRecipe>>,
    addToShoppingList: (List<PlannerRecipeWithRecipe>?) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        modifier = modifier,
        topBar = {
            MealPlannerTopBar(
                title = topBarTitle,
                onBack = onBack,
                onForward = onForward,
                onUndo = onUndo,
                undoVisible = undoVisible,
                onMenuClick = { coroutineScope.launch { drawerState.open() } }
            )
        },
        bottomBar = { MealPlannerBottomBar(onBulkEditClick = onBulkEditClick) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            repeat(7) {
                val date = CalendarUtils.datePlusOffset(startOfWeek, it)
                val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date)
                item {
                    var opened by remember { mutableStateOf(date.getDateString() == currentDate.getDateString()) }
                    WeekdayCard(
                        title = title,
                        onEdit = { navigateTo("${PlannerEditDestination.route}/${date.getDateString()}") },
                        cardColor = if (date.getDateString() == currentDate.getDateString()) MaterialTheme.colorScheme.secondary else CardDefaults.cardColors().containerColor,
                        opened = opened,
                        onOpen = { opened = !opened },
                        onShoppingList = { addToShoppingList(plannerRecipes[date.getDateString()]) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        plannerRecipes[date.getDateString()]?.forEach { recipe ->
                            PlannerRecipeCard(
                                recipe = recipe,
                                cardColor = if (date.getDateString() == currentDate.getDateString()) MaterialTheme.colorScheme.secondary else CardDefaults.cardColors().containerColor,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        navigateTo("${RecipeDetailsDestination.route}/${recipe.recipe.id}")
                                    }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlannerRecipeCard(
    recipe: PlannerRecipeWithRecipe,
    cardColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = recipe.plannerRecipe.type.getTitle(context).padEnd(20))
            Text(
                text = recipe.recipe.title,
                maxLines = 1,
                modifier = Modifier.weight(1f),
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun MealPlannerBottomBar(modifier: Modifier = Modifier, onBulkEditClick: () -> Unit) {
    BottomAppBar(modifier = modifier) {
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FilledTonalButton(
                    onClick = onBulkEditClick,
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = stringResource(R.string.bulkEdit)
                    )
                }
                Text(text = stringResource(R.string.bulkEdit))
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlannerTopBar(
    title: String,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onUndo: () -> Unit,
    undoVisible: Boolean,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 32.dp)
            ) {
                Text(text = title)
                Spacer(modifier = Modifier.weight(1f))
                if (undoVisible) {
                    IconButton(onClick = onUndo) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = stringResource(R.string.revertToCurrentWeek)
                        )
                    }
                }
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = stringResource(R.string.back)
                    )
                }
                IconButton(onClick = onForward) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = stringResource(R.string.forward)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = stringResource(R.string.openNavigationMenu)
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun WeekdayCard(
    title: String,
    onEdit: () -> Unit,
    cardColor: Color,
    opened: Boolean,
    onOpen: () -> Unit,
    onShoppingList: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onOpen) {
                    Icon(
                        imageVector = if (opened) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = stringResource(R.string.open_close_cards)
                    )
                }
                IconButton(onClick = onShoppingList) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = stringResource(R.string.addDayToShoppingList)
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(R.string.edit)
                    )
                }
            }
            if (opened) {
                Column {
                    content()
                }
            }
        }
    }
}
