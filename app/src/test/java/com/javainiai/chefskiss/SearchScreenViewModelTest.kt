package com.javainiai.chefskiss

import com.javainiai.chefskiss.data.enums.Sort
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.ui.components.search.SearchScreenViewModel
import com.javainiai.chefskiss.ui.components.search.SearchUiState
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SearchScreenViewModelTest {
    private lateinit var recipesRepository: RecipesRepository
    private lateinit var viewModel: SearchScreenViewModel

    @Before
    fun setup() {
        recipesRepository = mockk(relaxed = true)
        viewModel = SearchScreenViewModel(recipesRepository)
    }

    @Test
    fun `test updateQuery updates query in searchUiState`() = runBlocking {
        val query = "Chicken"
        viewModel.updateQuery(query)
        assertEquals(query, viewModel.searchUiState.value.query)
    }

    @Test
    fun `test updateRating updates rating in searchUiState`() = runBlocking {
        val rating = 5
        viewModel.updateRating(rating)
        assertEquals(rating, viewModel.searchUiState.value.rating)
    }

    @Test
    fun `test updateTags updates tags in searchUiState`() = runBlocking {
        val tags = listOf(Tag(title = "Healthy"))
        viewModel.updateTags(tags)
        assertEquals(tags, viewModel.searchUiState.value.selectedTags)
    }

    @Test
    fun `test updateSortingMethod updates sortingMethod in searchUiState`() = runBlocking {
        val method = Sort.RATING
        viewModel.updateSortingMethod(method)
        assertEquals(method, viewModel.searchUiState.value.sortingMethod)
    }

    @Test
    fun `test updateOrder updates order in searchUiState`() = runBlocking {
        val order = true
        viewModel.updateOrder(order)
        assertEquals(order, viewModel.searchUiState.value.ascending)
    }

    @Test
    fun `test updateFavorite updates favorite in searchUiState`() = runBlocking {
        val favorite = true
        viewModel.updateFavorite(favorite)
        assertEquals(favorite, viewModel.searchUiState.value.favorite)
    }

    @Test
    fun `test clear resets searchUiState to default`() = runBlocking {
        viewModel.clear()
        assertEquals(
            SearchUiState("", 0, listOf(), Sort.ADDED, false, false),
            viewModel.searchUiState.value
        )
    }
}