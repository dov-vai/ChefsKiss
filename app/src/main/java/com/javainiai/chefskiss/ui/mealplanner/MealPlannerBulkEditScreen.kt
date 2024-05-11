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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.CalendarUtils.getDateString
import com.javainiai.chefskiss.data.enums.BulkMode
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
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
) {
    var currentMode by remember {
        mutableStateOf(BulkMode.Select)
    }

    Scaffold(
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
                                BulkMode.Select -> {
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
                    BulkMode.Select -> {
                        Text(text = "Select meals")
                    }

                    else -> {
                        Text(text = title)
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Back"
                            )
                        }
                        IconButton(onClick = onForward) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Forward"
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
                    contentDescription = "Back to meal planner"
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
    onCopy: () -> Unit,
    onDone: () -> Unit,
    currentMode: BulkMode
) {
    val buttonColor =
        ButtonDefaults.filledTonalButtonColors(containerColor = MaterialTheme.colorScheme.surface)
    BottomAppBar(modifier = modifier) {
        Row {
            when (currentMode) {
                BulkMode.Select -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onSelectAll, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.SelectAll,
                                contentDescription = "Select meals"
                            )
                        }
                        Text(text = "Select All")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onDeselectAll, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.Deselect,
                                contentDescription = "Deselect meals"
                            )
                        }
                        Text(text = "Deselect All")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onMove, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.MoveDown,
                                contentDescription = "Move meals"
                            )
                        }
                        Text(text = "Move")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onCopy, colors = buttonColor) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy meals"
                            )
                        }
                        Text(text = "Copy")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }

                else -> {
                    Spacer(modifier = Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FilledTonalButton(onClick = onDone, colors = buttonColor) {
                            Icon(imageVector = Icons.Default.Done, contentDescription = "Done")
                        }
                        Text(text = "Done")
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
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Text(text = recipe.plannerRecipe.type.title.padEnd(20))
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
            Text(text = "+ Tap to paste here", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MoveHereCard(
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = "+ Tap to move here", fontWeight = FontWeight.Bold)
        }
    }
}