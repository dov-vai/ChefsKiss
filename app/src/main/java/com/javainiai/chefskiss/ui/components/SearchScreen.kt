package com.javainiai.chefskiss.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.javainiai.chefskiss.data.recipe.Recipe
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    customButtonIcon: ImageVector,
    onCustomButtonClick: () -> Unit,
    bottomBar: @Composable () -> Unit = {},
    viewModel: SearchScreenViewModel,
    content: @Composable (PaddingValues) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val filterDrawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val searchUiState by viewModel.searchUiState.collectAsState()
    val tags by viewModel.tags.collectAsState()
    FilterDrawer(drawerState = filterDrawerState, drawerContent = {
        FilterSheet(
            onClear = viewModel::clear, onApply = {
                viewModel.doSearch()
                coroutineScope.launch {
                    filterDrawerState.close()
                }
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            OrderSelection(
                ascending = searchUiState.ascending,
                updateOrder = viewModel::updateOrder,
                modifier = Modifier.fillMaxWidth()
            )
            RatingCard(
                rating = searchUiState.rating,
                onRatingChange = viewModel::updateRating,
                modifier = Modifier.fillMaxWidth()
            )
            FavoriteButton(
                favorite = searchUiState.favorite,
                updateFavorite = viewModel::updateFavorite,
                modifier = Modifier.fillMaxWidth()
            )
            SortCard(
                selectedSort = searchUiState.sortingMethod,
                updateSortingMethod = viewModel::updateSortingMethod,
                modifier = Modifier.fillMaxWidth()
            )
            TagCard(
                tags = tags,
                selectedTags = searchUiState.selectedTags,
                updateTags = viewModel::updateTags,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }) {
        Scaffold(
            modifier = modifier,
            topBar = {
                SearchTopBar(
                    query = searchUiState.query,
                    onQueryChange = viewModel::updateQuery,
                    customButtonIcon = customButtonIcon,
                    onCustomButtonClick = onCustomButtonClick,
                    onSearch = viewModel::doSearch,
                    onFilterClick = { coroutineScope.launch { filterDrawerState.open() } }
                )
            },
            bottomBar = bottomBar
        ) {
            content(it)
        }
    }
}

@Composable
fun RecipeCard(recipe: Recipe, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(
                5.dp
            )
        )
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
                modifier = Modifier.padding(8.dp).weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = recipe.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
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
            Icon(
                imageVector = if (recipe.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Favorite recipe"
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    customButtonIcon: ImageVector,
    onCustomButtonClick: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    Surface(modifier = modifier) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onSearch()
                focusManager.clearFocus()
            }),
            singleLine = true,
            leadingIcon = {
                IconButton(onClick = onCustomButtonClick) {
                    Icon(imageVector = customButtonIcon, contentDescription = null)
                }
            },
            trailingIcon = {
                if (query != "") {
                    IconButton(onClick = {
                        onQueryChange("")
                        onSearch()
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(32.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                focusedContainerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp),
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            placeholder = { Text(text = "Search") }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBar(
    query: String,
    onQueryChange: (String) -> Unit,
    customButtonIcon: ImageVector,
    onCustomButtonClick: () -> Unit,
    onSearch: () -> Unit,
    onFilterClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SearchBar(
                    query = query,
                    onQueryChange = onQueryChange,
                    customButtonIcon = customButtonIcon,
                    onCustomButtonClick = onCustomButtonClick,
                    onSearch = onSearch,
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp)
                )
                IconButton(onClick = onFilterClick) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter")
                }
            }

        },
        modifier = Modifier.height(70.dp)
    )
}