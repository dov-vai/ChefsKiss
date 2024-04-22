package com.javainiai.chefskiss

import com.javainiai.chefskiss.data.CalendarUtils
import com.javainiai.chefskiss.data.recipe.RecipesRepository
import com.javainiai.chefskiss.ui.mealplanner.MealPlannerViewModel
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class MealPlannerViewModelTest {
    private lateinit var recipesRepository: RecipesRepository
    private lateinit var viewModel: MealPlannerViewModel

    @Before
    fun setup() {
        recipesRepository = mockk(relaxed = true)
        viewModel = MealPlannerViewModel(recipesRepository)
    }

    @Test
    fun `test initial title`() = runBlocking {
        val expectedTitle = viewModel.titleFormat()
        assertEquals(expectedTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `test shiftForward updates startOfWeek and title`() = runBlocking {
        val initialStartOfWeek = viewModel.uiState.value.startOfWeek
        viewModel.shiftForward()
        val expectedStartOfWeek = CalendarUtils.datePlusOffset(initialStartOfWeek, 7)
        assertEquals(expectedStartOfWeek, viewModel.uiState.value.startOfWeek)
        val expectedTitle = viewModel.titleFormat()
        assertEquals(expectedTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `test shiftBackwards updates startOfWeek and title`() = runBlocking {
        val initialStartOfWeek = viewModel.uiState.value.startOfWeek
        viewModel.shiftBackwards()
        val expectedStartOfWeek = CalendarUtils.datePlusOffset(initialStartOfWeek, -7)
        assertEquals(expectedStartOfWeek, viewModel.uiState.value.startOfWeek)
        val expectedTitle = viewModel.titleFormat()
        assertEquals(expectedTitle, viewModel.uiState.value.title)
    }

    @Test
    fun `test revertStartOfWeek updates startOfWeek and title`() = runBlocking {
        viewModel.revertStartOfWeek()
        val expectedStartOfWeek = CalendarUtils.getStartOfWeek()
        assertEquals(expectedStartOfWeek, viewModel.uiState.value.startOfWeek)
        val expectedTitle = viewModel.titleFormat()
        assertEquals(expectedTitle, viewModel.uiState.value.title)
    }
}