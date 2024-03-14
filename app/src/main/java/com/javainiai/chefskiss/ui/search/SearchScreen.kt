package com.javainiai.chefskiss.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.AppViewModelProvider
import com.javainiai.chefskiss.ui.navigation.NavigationDestination

object SearchDestination : NavigationDestination {
    override val route = "search"
}

@Composable
fun SearchScreen(
    navigateBack: () -> Unit,
    viewModel: SearchScreenViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val tags by viewModel.tags.collectAsState()

    Scaffold(topBar = {
        TopSearchBar(
            uiState.query,
            viewModel::updateQuery,
            viewModel::doSearch,
            uiState.searchActive,
            viewModel::updateActive,
            navigateBack
        )
    }) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OrderSelection(
                ascending = uiState.ascending,
                updateOrder = viewModel::updateOrder,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            RatingCard(
                rating = uiState.rating,
                onRatingChange = viewModel::updateRating,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            SortCard(
                selectedSort = uiState.sortingMethod,
                updateSortingMethod = viewModel::updateSortingMethod,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
            TagCard(
                tags = tags,
                selectedTags = uiState.selectedTags,
                updateTags = viewModel::updateTags,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderSelection(
    ascending: Boolean,
    updateOrder: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Order by")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = ascending,
                    onClick = { updateOrder(true) },
                    label = { Text(text = "Ascending") })
                FilterChip(
                    selected = !ascending,
                    onClick = { updateOrder(false) },
                    label = { Text(text = "Descending") })
            }
        }
    }
}

data class SortingMethod(
    val title: String,
    val method: Sort
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SortCard(
    selectedSort: Sort,
    updateSortingMethod: (Sort) -> Unit,
    modifier: Modifier = Modifier
) {
    var opened by remember {
        mutableStateOf(false)
    }

    val chips = mutableListOf(
        SortingMethod("Time added", Sort.ADDED),
        SortingMethod("Cooking time", Sort.COOKING_TIME),
        SortingMethod("Rating", Sort.RATING),
        SortingMethod("Portion size", Sort.PORTION)
    )

    OutlinedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Sort by")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { opened = !opened }) {
                    Icon(
                        imageVector = if (opened) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Open/close sorting methods"
                    )
                }
            }
            if (opened) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chips.forEach {
                        FilterChip(
                            selected = selectedSort == it.method,
                            onClick = { updateSortingMethod(it.method) },
                            label = { Text(it.title) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TagCard(
    tags: List<Tag>,
    selectedTags: List<Tag>,
    updateTags: (List<Tag>) -> Unit,
    modifier: Modifier = Modifier
) {
    var opened by remember {
        mutableStateOf(false)
    }
    OutlinedCard(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "Tag filter")
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { opened = !opened }) {
                    Icon(
                        imageVector = if (opened) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = "Open/close tags"
                    )
                }
            }
            if (opened) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach {
                        FilterChip(selected = selectedTags.contains(it),
                            onClick = {
                                if (selectedTags.contains(it))
                                    updateTags(selectedTags - it)
                                else
                                    updateTags(selectedTags + it)
                            },
                            label = { Text(text = it.title) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RatingCard(rating: Int, onRatingChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = "Rating")
            Row {
                repeat(rating) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.clickable { onRatingChange(it + 1) })
                }
                repeat(5 - rating) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.clickable { onRatingChange(rating + it + 1) })
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(title = {
        SearchBar(
            modifier = modifier,
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            leadingIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear text")
                }
            }
        ) {

        }
    })

}