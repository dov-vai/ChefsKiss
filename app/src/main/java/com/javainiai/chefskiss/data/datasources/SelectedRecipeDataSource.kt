package com.javainiai.chefskiss.data.datasources

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectedRecipeDataSource {

    private val _recipeId = MutableStateFlow(null as Long?)

    val recipeId = _recipeId.asStateFlow()

    fun updateRecipeId(recipeId: Long) {
        _recipeId.update { recipeId }
    }

}