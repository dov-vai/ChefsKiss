package com.javainiai.chefskiss.ui.mealplanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.data.enums.Meal
import com.javainiai.chefskiss.data.recipe.PlannerRecipeWithRecipe
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.homescreen.RecipeCard
import com.javainiai.chefskiss.ui.navigation.NavigationDestination

object PlannerEditDestination : NavigationDestination {
    override val route = "plannerEdit"
    const val plannerDateArg = "plannerDate"
    val routeWithArgs = "$route/{$plannerDateArg}"
}

@Composable
fun PlannerEditScreen(
    navigateBack: () -> Unit,
    viewModel: PlannerEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val plannerRecipes by viewModel.plannerRecipes.collectAsState()
    val selectedRecipe by viewModel.selectedRecipe.collectAsState()

    Scaffold(topBar = {
        PlannerEditTopBar(
            title = uiState.title,
            navigateBack = navigateBack
        )
    }) { padding ->
        Column(
            modifier = Modifier.padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Meal.entries.forEach {
                    FilterChip(
                        selected = uiState.selectedType == it,
                        onClick = { viewModel.updateType(it) },
                        label = { Text(text = it.title) })
                }
            }
            selectedRecipe?.let {
                RecipeCard(recipe = it, modifier = Modifier.padding(16.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilledTonalButton(onClick = { /*TODO*/ }) {
                    Text(text = "Select Recipe")
                }
                FilledTonalButton(onClick = viewModel::insertPlannerRecipe) {
                    Text(text = "Add To Planner")
                }
            }
            HorizontalDivider()
            LazyColumn {
                items(plannerRecipes) {
                    PlannerRecipeCard(
                        recipe = it,
                        onDelete = { viewModel.deletePlannerRecipe(it.plannerRecipe) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerEditTopBar(title: String, navigateBack: () -> Unit, modifier: Modifier = Modifier) {
    CenterAlignedTopAppBar(title = { Text(text = title) },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }
    )
}

@Composable
fun PlannerRecipeCard(
    recipe: PlannerRecipeWithRecipe,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
            Text(text = recipe.plannerRecipe.type.title)
            Spacer(modifier = Modifier.weight(1f))
            Text(text = recipe.recipe.title)
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onDelete) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete recipe")
            }
        }
    }
}