package com.javainiai.chefskiss.ui.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.enums.Sort
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

data class HomeUiState(
    val recipeList: List<Recipe>
)

data class SearchUiState(
    val query: String,
    val rating: Int,
    val selectedTags: List<Tag>,
    val sortingMethod: Sort,
    val ascending: Boolean,
    val favorite: Boolean
)

class HomeScreenViewModel(val recipesRepository: RecipesRepository) : ViewModel() {
    private var _finalSearch =
        MutableStateFlow(SearchUiState("", 0, listOf(), Sort.ADDED, false, false))

    @OptIn(ExperimentalCoroutinesApi::class)
    val homeUiState: StateFlow<HomeUiState> = _finalSearch.flatMapLatest {
        withContext(Dispatchers.IO) {
            recipesRepository.query(
                recipeName = it.query,
                rating = it.rating,
                isAsc = it.ascending,
                sortingMethod = it.sortingMethod,
                tags = it.selectedTags,
                favorite = it.favorite
            )
        }
    }
        .map { HomeUiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = HomeUiState(listOf())
        )

    private var _searchUiState =
        MutableStateFlow(SearchUiState("", 0, listOf(), Sort.ADDED, false, false))

    val searchUiState = _searchUiState.asStateFlow()

    val tags: StateFlow<List<Tag>> = recipesRepository
        .getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    fun updateQuery(query: String) {
        _searchUiState.update { currentState ->
            currentState.copy(
                query = query
            )
        }
    }

    fun updateRating(rating: Int) {
        _searchUiState.update { currentState ->
            currentState.copy(
                rating = rating
            )
        }
    }

    fun updateTags(tags: List<Tag>) {
        _searchUiState.update { currentState ->
            currentState.copy(
                selectedTags = tags
            )
        }
    }

    fun updateSortingMethod(method: Sort) {
        _searchUiState.update { currentState ->
            currentState.copy(
                sortingMethod = method
            )
        }
    }

    fun updateOrder(ascending: Boolean) {
        _searchUiState.update { currentState ->
            currentState.copy(
                ascending = ascending
            )
        }
    }

    fun updateFavorite(favorite: Boolean) {
        _searchUiState.update { currentState ->
            currentState.copy(
                favorite = favorite
            )
        }
    }

    fun clear() {
        _searchUiState.update { currentState ->
            currentState.copy(
                rating = 0,
                selectedTags = listOf(),
                sortingMethod = Sort.ADDED,
                ascending = false,
                favorite = false
            )
        }
    }


    fun doSearch() {
        _finalSearch.update { currentState ->
            with(_searchUiState.value) {
                currentState.copy(
                    query = query,
                    rating = rating,
                    selectedTags = selectedTags,
                    sortingMethod = sortingMethod,
                    ascending = ascending,
                    favorite = favorite
                )
            }
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}


