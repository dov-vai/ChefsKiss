package com.javainiai.chefskiss.ui.selectionscreen

import androidx.lifecycle.ViewModel
import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource

class SelectionScreenViewModel(private val selectedRecipeDataSource: SelectedRecipeDataSource) :
    ViewModel() {
    fun setSelectedRecipe(recipeId: Long) {
        selectedRecipeDataSource.updateRecipeId(recipeId)
    }
}