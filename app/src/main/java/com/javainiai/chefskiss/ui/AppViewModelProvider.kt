package com.javainiai.chefskiss.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.javainiai.chefskiss.ChefsKissApplication
import com.javainiai.chefskiss.ui.components.SearchScreenViewModel
import com.javainiai.chefskiss.ui.homescreen.HomeScreenViewModel
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerViewModel
import com.javainiai.chefskiss.ui.mealplanner.PlannerEditViewModel
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeViewModel
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsViewModel
import com.javainiai.chefskiss.ui.selectionscreen.SelectionScreenViewModel
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel()
        }
        initializer {
            AddRecipeViewModel(chefsKissApplication().container.recipesRepository)
        }
        initializer {
            RecipeDetailsViewModel(
                this.createSavedStateHandle(),
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            ShoppingListViewModel(chefsKissApplication().container.recipesRepository)
        }
        initializer {
            MealPlannerViewModel(chefsKissApplication().container.recipesRepository)
        }
        initializer {
            PlannerEditViewModel(
                chefsKissApplication().container.selectedRecipeDataSource,
                this.createSavedStateHandle(),
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            SearchScreenViewModel(
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            SelectionScreenViewModel(chefsKissApplication().container.selectedRecipeDataSource)
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ChefsKissApplication].
 */
fun CreationExtras.chefsKissApplication(): ChefsKissApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChefsKissApplication)