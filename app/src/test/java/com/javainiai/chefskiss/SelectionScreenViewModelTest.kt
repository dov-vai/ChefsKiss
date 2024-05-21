package com.javainiai.chefskiss

import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource
import com.javainiai.chefskiss.ui.selectionscreen.SelectionScreenViewModel
import io.mockk.mockk
import io.mockk.verify
import org.junit.Test

class SelectionScreenViewModelTest {

    private val selectedRecipeDataSource = mockk<SelectedRecipeDataSource>(relaxed = true)
    private val viewModel = SelectionScreenViewModel(selectedRecipeDataSource)

    @Test
    fun setSelectedRecipe_updatesRecipeIdInDataSource() {
        val recipeId = 1L

        viewModel.setSelectedRecipe(recipeId)

        verify { selectedRecipeDataSource.updateRecipeId(recipeId) }
    }
}