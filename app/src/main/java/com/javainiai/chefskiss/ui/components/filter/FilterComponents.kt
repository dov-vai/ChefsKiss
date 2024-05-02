package com.javainiai.chefskiss.ui.components.filter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.javainiai.chefskiss.data.enums.Sort
import com.javainiai.chefskiss.data.tag.Tag

@Composable
fun FilterDrawer(
    drawerState: DrawerState,
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    // Setting the layout to go from right to left is a hack
    // As Jetpack Compose doesn't (yet) provide a side drawer that goes from right to left
    CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerContent = drawerContent,
            drawerState = drawerState,
            modifier = modifier
        ) {
            CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Ltr) {
                content()
            }
        }
    }
}

@Composable
fun FilterSheet(
    onClear: () -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    ModalDrawerSheet {
        CompositionLocalProvider(value = LocalLayoutDirection provides LayoutDirection.Ltr) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Filters")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = onClear) {
                        Text(text = "Clear")
                    }
                    TextButton(onClick = onApply) {
                        Text(text = "Apply")
                    }
                }
                content()
            }
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
                    Sort.entries.forEach {
                        FilterChip(
                            selected = selectedSort == it,
                            onClick = { updateSortingMethod(it) },
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
                        modifier = Modifier.clickable { onRatingChange(it + 1) }
                    )
                }
                repeat(5 - rating) {
                    Icon(
                        imageVector = Icons.Default.StarBorder,
                        contentDescription = null,
                        modifier = Modifier.clickable { onRatingChange(rating + it + 1) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteButton(
    favorite: Boolean,
    updateFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row {
                FilterChip(
                    selected = favorite,
                    onClick = { updateFavorite(!favorite) },
                    label = { Text(text = "Favorite") })
            }
        }
    }
}