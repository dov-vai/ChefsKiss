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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Deselect
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.MoveDown
import androidx.compose.material.icons.filled.SelectAll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.javainiai.chefskiss.R
import com.javainiai.chefskiss.data.database.planner.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.data.enums.BulkMode
import com.javainiai.chefskiss.data.utils.CalendarUtils
import com.javainiai.chefskiss.data.utils.CalendarUtils.getDateString
import com.javainiai.chefskiss.ui.components.ConfirmationDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MealPlannerBulkEditScreen(
    modifier: Modifier = Modifier,
    startOfWeek: Date,
    topBarTitle: String,
    plannerRecipes: Map<String, List<PlannerRecipeWithRecipe>>,
    selectedRecipes: List<PlannerRecipeWithRecipe>,
    updateSelectedRecipes: (List<PlannerRecipeWithRecipe>) -> Unit,
    onNavigateBack: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onDone: () -> Unit,
    pasteMeals: (Date) -> Unit,
    moveMeals: (Date) -> Unit,
    deleteMeals: () -> Unit
) {
    var currentMode by remember {
        mutableStateOf(BulkMode.Select)
    }

    if (currentMode == BulkMode.Delete) {
        ConfirmationDialog(
            onDismiss = { currentMode = BulkMode.Select },
            onConfirm = {
                deleteMeals()
                currentMode = BulkMode.Select
            },
            onDismissRequest = { currentMode = BulkMode.Select },
            text = {
                Text(
                    text = stringResource(
                        R.string.bulk_edit_delete_confirmation,
                        selectedRecipes.count()
                    )
                )
            })
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            BulkEditTopBar(
                title = topBarTitle,
                onNavigateBack = onNavigateBack,
                currentMode = currentMode,
                onBack = onBack,
                onForward = onForward
            )
        },
        bottomBar = {
            BulkEditBottomBar(
                onSelectAll = {
                    updateSelectedRecipes(plannerRecipes.values.flatten())
                },
                onDeselectAll = {
                    updateSelectedRecipes(listOf())
                },
                onDelete = { currentMode = BulkMode.Delete },
                onCopy = { currentMode = BulkMode.Copy },
                onMove = { currentMode = BulkMode.Move },
                onDone = {
                    currentMode = BulkMode.Select
                    onDone()
                },
                currentMode = currentMode
            )
        },
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            repeat(7) {
                val date = CalendarUtils.datePlusOffset(startOfWeek, it)
                val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date)
                item {
                    SelectionWeekdayCard(
                        title = title,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        plannerRecipes[date.getDateString()]?.forEach { recipe ->
                            when (currentMode) {
                                BulkMode.Select, BulkMode.Delete -> {
                                    SelectionPlannerRecipeCard(
                                        recipe = recipe,
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        checked = selectedRecipes.contains(recipe),
                                        onCheckedChange = {
                                            if (it) {
                                                updateSelectedRecipes(selectedRecipes + recipe)
                                            } else {
                                                updateSelectedRecipes(selectedRecipes - recipe)
                                            }
                                        }
                                    )
                                }

                                else -> {
                                    PlannerRecipeCard(
                                        recipe = recipe,
                                        cardColor = CardDefaults.cardColors().containerColor
                                    )
                                }
                            }
                        }
                        when (currentMode) {
                            BulkMode.Copy -> PasteHereCard(modifier = Modifier.clickable {
                                pasteMeals(
                                    date
                                )
                            })

                            BulkMode.Move -> MoveHereCard(modifier = Modifier.clickable {
                                moveMeals(
                                    date
                                )
                            })

                            else -> {}
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BulkEditTopBar(
    modifier: Modifier = Modifier,
    title: String,
    onNavigateBack: () -> Unit,
    currentMode: BulkMode,
    onBack: () -> Unit,
    onForward: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (currentMode) {
                    BulkMode.Select, BulkMode.Delete -> {
                        Text(text = stringResource(R.string.select_meals))
                    }

                    else -> {
                        Text(text = title)
                        Spacer(modifier = Modifier.weight(1f))
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
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.backToMealPlanner)
                )
            }
        }
    )
}


@Composable
fun BulkEditBottomBar(
    modifier: Modifier = Modifier,
    onSelectAll: () -> Unit,
    onDeselectAll: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onDone: () -> Unit,
    currentMode: BulkMode
) {
    val buttonColor =
        ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surface)
    BottomAppBar(modifier = modifier) {
        Row {
            when (currentMode) {
                BulkMode.Select, BulkMode.Delete -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onSelectAll, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = stringResource(R.string.select_meals)
                            )
                        }
                        Text(text = stringResource(R.string.selectAll))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onDeselectAll, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.Deselect,
                                contentDescription = stringResource(R.string.deselect_meals)
                            )
                        }
                        Text(text = stringResource(R.string.deselectAll))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onMove, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.MoveDown,
                                contentDescription = stringResource(R.string.moveMeals)
                            )
                        }
                        Text(text = stringResource(R.string.move))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onDelete, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = stringResource(R.string.delete_meals)
                            )
                        }
                        Text(text = stringResource(R.string.delete))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onCopy, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = stringResource(R.string.copyMeals)
                            )
                        }
                        Text(text = stringResource(R.string.copy))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                else -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onDone, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = stringResource(R.string.done)
                            )
                        }
                        Text(text = stringResource(R.string.done))
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }


        }
    }
}

@Composable
fun SelectionWeekdayCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title)
            }
            Column {
                content()
            }
        }
    }
}

@Composable
fun SelectionPlannerRecipeCard(
    modifier: Modifier = Modifier,
    recipe: PlannerRecipeWithRecipe,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
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
fun PasteHereCard(
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = stringResource(R.string.tapToPasteHere), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MoveHereCard(
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = stringResource(R.string.tapToMoveHere), fontWeight = FontWeight.Bold)
        }
    }
}