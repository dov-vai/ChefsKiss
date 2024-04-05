package com.javainiai.chefskiss.ui.mealplanner

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination
import java.text.SimpleDateFormat
import java.util.Locale

object MealPlannerDestination : NavigationDestination {
    override val route = "mealplanner"
}

@Composable
fun MealPlannerScreen(
    navigateBack: () -> Unit,
    viewModel: MealPlannerViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateTo: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val plannerRecipes by viewModel.plannerRecipes.collectAsState()
    Scaffold(topBar = {
        MealPlannerTopBar(
            title = uiState.title,
            onBack = {},
            onForward = {},
            navigateBack = navigateBack
        )
    }) { padding ->
        LazyColumn(contentPadding = padding) {
            repeat(7) {
                val date = CalendarUtils.datePlusOffset(uiState.startOfWeek, it)
                val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(date)
                val fullDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                item {
                    var opened by remember { mutableStateOf(date == uiState.currentDate) }
                    WeekdayCard(
                        title = title,
                        onEdit = { navigateTo("${PlannerEditDestination.route}/$fullDate") },
                        cardColor = if (date == uiState.currentDate) MaterialTheme.colorScheme.secondary else CardDefaults.cardColors().containerColor,
                        opened = opened,
                        onOpen = { opened = !opened },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        plannerRecipes[fullDate]?.forEach { recipe ->
                            PlannerRecipeCard(
                                recipe = recipe,
                                cardColor = if (date == uiState.currentDate) MaterialTheme.colorScheme.secondary else CardDefaults.cardColors().containerColor,
                                modifier = Modifier.fillMaxWidth()
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
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = cardColor)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = recipe.plannerRecipe.type.title)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = recipe.recipe.title)
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
    navigateBack: () -> Unit,
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
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        contentDescription = "Open/Close card"
                    )
                }
                IconButton(onClick = onEdit) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
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