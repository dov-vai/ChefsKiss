package com.javainiai.chefskiss

import android.content.Context
import com.javainiai.chefskiss.data.ChefsKissAppContainer
import io.mockk.mockk
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test

class ChefsKissAppContainerTest {

    private lateinit var context: Context
    private lateinit var appContainer: ChefsKissAppContainer

    @Before
    fun setup() {
        context = mockk(relaxed = true)
        appContainer = ChefsKissAppContainer(context)
    }

    @Test
    fun `recipesService is initialized`() {
        assertNotNull(appContainer.recipeService)
    }

    @Test
    fun `plannerService is initialized`() {
        assertNotNull(appContainer.plannerService)
    }

    @Test
    fun `shoppingListService is initialized`() {
        assertNotNull(appContainer.shoppingListService)
    }

    @Test
    fun `configService is initialized`() {
        assertNotNull(appContainer.configService)
    }

    @Test
    fun `selectedRecipeDataSource is initialized`() {
        assertNotNull(appContainer.selectedRecipeDataSource)
    }
}