package com.javainiai.chefskiss

import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.hasAnySibling
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.CalendarUtils.getDateString
import com.javainiai.chefskiss.ui.ChefsKissApp
import com.javainiai.chefskiss.ui.navigation.NavDrawerNavigatorDestination
import com.javainiai.chefskiss.ui.recipescreen.AddRecipeDestination
import com.javainiai.chefskiss.ui.recipescreen.RecipeDetailsDestination
import com.javainiai.chefskiss.ui.selectionscreen.SelectionDestination
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ScreenNavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: TestNavHostController

    @Before
    fun setupNavHost() {
        composeTestRule.setContent {
            navController = TestNavHostController(LocalContext.current).apply {
                navigatorProvider.addNavigator(ComposeNavigator())
            }
            ChefsKissApp(navController = navController)
        }
    }

    @Test
    fun navHost_verifyStartDestination() {
        navController.assertCurrentRouteName(NavDrawerNavigatorDestination.route)
    }

    @Test
    fun navHost_clickOnAddRecipe_navigatesToAddRecipeScreen() {
        composeTestRule.onNodeWithContentDescription("Add recipe").performClick()
        navController.assertCurrentRouteName(AddRecipeDestination.route)
    }

    @Test
    fun navHost_clickEditTodayInMealPlanner_navigatesToTodaysEditScreen() {
        composeTestRule.onNodeWithContentDescription("Search button").performClick()
        composeTestRule.onNodeWithText("Meal Planner").performClick()
        val currentDate = CalendarUtils.getCurrentDate()
        val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(currentDate)
        composeTestRule.onNode(
            hasContentDescription("Edit") and
                    hasAnySibling(hasText(title))
        ).performClick()
        composeTestRule.onNodeWithText(currentDate.getDateString()).assertExists()
    }

    private fun addRecipe(title: String, cookingTime: String, servings: String) {
        composeTestRule.onNodeWithContentDescription("Add recipe").performClick()
        composeTestRule.onNodeWithText("Title").performTextInput(title)
        composeTestRule.onNodeWithText("Cooking Time (minutes)").performTextInput(cookingTime)
        composeTestRule.onNodeWithText("Servings").performTextInput(servings)
        composeTestRule.onNodeWithContentDescription("Done").performClick()
    }

    @Test
    fun navHost_clickOnRecipeCard_navigatesToRecipeDetails() {
        val title = "Pancakes"
        val cookingTime = "20"
        val servings = "2"
        addRecipe(title, cookingTime, servings)
        composeTestRule.onNodeWithText(title).performClick()
        navController.assertCurrentRouteName(RecipeDetailsDestination.routeWithArgs)
        composeTestRule.onNodeWithText(title).assertExists()
    }

    @Test
    fun navHost_clickOnSelectRecipeInMealPlannerEdit_navigatesToSelectionScreen() {
        composeTestRule.onNodeWithContentDescription("Search button").performClick()
        composeTestRule.onNodeWithText("Meal Planner").performClick()
        val currentDate = CalendarUtils.getCurrentDate()
        val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(currentDate)
        composeTestRule.onNode(
            hasContentDescription("Edit") and
                    hasAnySibling(hasText(title))
        ).performClick()
        composeTestRule.onNodeWithText("Select Recipe").performClick()
        navController.assertCurrentRouteName(SelectionDestination.route)
    }

    @Test
    fun navHost_clickOnMealPlannerRecipe_navigatesToRecipeDetails() {
        val recipe = "Omelette"
        val cookingTime = "20"
        val servings = "2"
        addRecipe(recipe, cookingTime, servings)
        composeTestRule.onNodeWithContentDescription("Search button").performClick()
        composeTestRule.onNodeWithText("Meal Planner").performClick()
        val currentDate = CalendarUtils.getCurrentDate()
        val title = SimpleDateFormat("EEEE, dd MMM", Locale.getDefault()).format(currentDate)
        composeTestRule.onNode(
            hasContentDescription("Edit") and
                    hasAnySibling(hasText(title))
        ).performClick()
        composeTestRule.onNodeWithText("Select Recipe").performClick()
        composeTestRule.onNodeWithText(recipe).performClick()
        composeTestRule.onNodeWithText("Add To Planner").performClick()
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        composeTestRule.onNodeWithText(recipe).performClick()
        navController.assertCurrentRouteName(RecipeDetailsDestination.routeWithArgs)
        composeTestRule.onNodeWithText(recipe).assertExists()
    }

}