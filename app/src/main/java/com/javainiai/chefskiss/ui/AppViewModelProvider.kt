package com.javainiai.chefskiss.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.javainiai.chefskiss.ChefsKissApplication
import com.javainiai.chefskiss.ui.app.ChefsKissAppViewModel
import com.javainiai.chefskiss.ui.components.search.SearchScreenViewModel
import com.javainiai.chefskiss.ui.homescreen.HomeScreenViewModel
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerViewModel
import com.javainiai.chefskiss.ui.mealplanner.PlannerEditViewModel
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeViewModel
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsViewModel
import com.javainiai.chefskiss.ui.selectionscreen.SelectionScreenViewModel
import com.javainiai.chefskiss.ui.shoppinglist.ShoppingListViewModel
import com.javainiai.chefskiss.ui.timer.TimerViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeScreenViewModel()
        }
        initializer {
            AddRecipeViewModel(
                this.createSavedStateHandle(),
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            RecipeDetailsViewModel(
                this.chefsKissApplication().container.context,
                this.createSavedStateHandle(),
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            ShoppingListViewModel(
                chefsKissApplication().container.context,
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            MealPlannerViewModel(
                chefsKissApplication().container.context,
                chefsKissApplication().container.recipesRepository
            )
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
        initializer {
            TimerViewModel(
                this.createSavedStateHandle(),
                chefsKissApplication().container.recipesRepository
            )
        }
        initializer {
            ChefsKissAppViewModel(
                chefsKissApplication().container.context,
                chefsKissApplication().container.configRepository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [ChefsKissApplication].
 */
fun CreationExtras.chefsKissApplication(): ChefsKissApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ChefsKissApplication)