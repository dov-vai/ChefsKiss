package com.javainiai.chefskiss.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.data.tag.Tag
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class SearchUiState(
    val query: String,
    val searchActive: Boolean,
    val rating: Int,
    val selectedTags: List<Tag>,
    val sortingMethod: Sort,
    val ascending: Boolean
)

enum class Sort {
    COOKING_TIME,
    RATING,
    ADDED,
    PORTION
}

class SearchScreenViewModel(recipesRepository: RecipesRepository) : ViewModel() {
    private var _uiState =
        MutableStateFlow(SearchUiState("", false, 0, listOf(), Sort.ADDED, false))
    val uiState = _uiState.asStateFlow()

    val tags: StateFlow<List<Tag>> = recipesRepository
        .getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = listOf()
        )

    fun updateQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                query = query
            )
        }
    }

    fun updateActive(active: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                searchActive = active
            )
        }
    }

    fun updateRating(rating: Int) {
        _uiState.update { currentState ->
            currentState.copy(
                rating = rating
            )
        }
    }

    fun updateTags(tags: List<Tag>) {
        _uiState.update { currentState ->
            currentState.copy(
                selectedTags = tags
            )
        }
    }

    fun updateSortingMethod(method: Sort) {
        _uiState.update { currentState ->
            currentState.copy(
                sortingMethod = method
            )
        }
    }

    fun updateOrder(ascending: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(
                ascending = ascending
            )
        }
    }

    fun doSearch(search: String) {

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}