package com.javainiai.chefskiss.ui.selectionscreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.components.search.RecipeCard
import com.javainiai.chefskiss.ui.components.search.SearchScreen
import com.javainiai.chefskiss.ui.components.search.SearchScreenViewModel
import com.javainiai.chefskiss.ui.navigation.NavigationDestination

object SelectionDestination : NavigationDestination {
    override val route = "selectRecipe"
}

@Composable
fun SelectionScreen(
    navigateBack: () -> Unit,
    viewModel: SelectionScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    searchViewModel: SearchScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val recipes by searchViewModel.recipes.collectAsState()
    SearchScreen(
        customButtonIcon = Icons.AutoMirrored.Filled.ArrowBack,
        onCustomButtonClick = navigateBack,
        viewModel = searchViewModel
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .clickable {
                            viewModel.setSelectedRecipe(recipe.id)
                            navigateBack()
                        }
                )
            }
        }
    }
}