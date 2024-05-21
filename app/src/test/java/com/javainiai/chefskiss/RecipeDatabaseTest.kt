package com.javainiai.chefskiss

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.javainiai.chefskiss.data.RecipeDatabase
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class RecipeDatabaseTest {
    private lateinit var db: RecipeDatabase
    private lateinit var context: Context

    @Before
    fun createDb() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, RecipeDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun testRecipeDao() {
        val recipeDao = db.RecipeDao()
        assertNotNull(recipeDao)
    }

    @Test
    fun testIngredientDao() {
        val ingredientDao = db.IngredientDao()
        assertNotNull(ingredientDao)
    }

    @Test
    fun testTagDao() {
        val tagDao = db.TagDao()
        assertNotNull(tagDao)
    }

    @Test
    fun testGetDatabase() {
        val instance1 = RecipeDatabase.getDatabase(context)
        val instance2 = RecipeDatabase.getDatabase(context)
        // should return the same instance every time
        assertEquals(instance1, instance2)
    }
}