package com.javainiai.chefskiss

import com.javainiai.chefskiss.data.datasources.SelectedRecipeDataSource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test


class SelectedRecipeDataSourceTest {

    private val dataSource = SelectedRecipeDataSource()

    @Test
    fun `updateRecipeId updates the recipeId`() = runBlocking {
        // Given
        val expectedRecipeId = 123L

        // When
        dataSource.updateRecipeId(expectedRecipeId)

        // Then
        val actualRecipeId = dataSource.recipeId.first()
        assertEquals(expectedRecipeId, actualRecipeId)
    }
}