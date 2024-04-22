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
    fun `recipesRepository is initialized`() {
        assertNotNull(appContainer.recipesRepository)
    }

    @Test
    fun `selectedRecipeDataSource is initialized`() {
        assertNotNull(appContainer.selectedRecipeDataSource)
    }
}