package com.javainiai.chefskiss.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.javainiai.chefskiss.data.ingredient.Ingredient
import com.javainiai.chefskiss.data.ingredient.IngredientDao
import com.javainiai.chefskiss.data.recipe.Recipe
import com.javainiai.chefskiss.data.recipe.RecipeDao
import com.javainiai.chefskiss.data.tag.Tag
import com.javainiai.chefskiss.data.tag.TagDao

@Database(
    entities = [Recipe::class, Ingredient::class, Tag::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun RecipeDao(): RecipeDao
    abstract fun IngredientDao(): IngredientDao
    abstract fun TagDao(): TagDao

    companion object {
        @Volatile
        private var Instance: RecipeDatabase? = null
        fun getDatabase(context: Context): RecipeDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, RecipeDatabase::class.java, "recipe_database")
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}