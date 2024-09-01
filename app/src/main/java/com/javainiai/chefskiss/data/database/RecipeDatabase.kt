package com.javainiai.chefskiss.data.database

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.javainiai.chefskiss.data.database.config.Config
import com.javainiai.chefskiss.data.database.config.ConfigDao
import com.javainiai.chefskiss.data.database.ingredient.Ingredient
import com.javainiai.chefskiss.data.database.ingredient.IngredientDao
import com.javainiai.chefskiss.data.database.planner.PlannerDao
import com.javainiai.chefskiss.data.database.planner.PlannerRecipe
import com.javainiai.chefskiss.data.database.recipe.Recipe
import com.javainiai.chefskiss.data.database.recipe.RecipeDao
import com.javainiai.chefskiss.data.database.recipe.RecipeTagCrossRef
import com.javainiai.chefskiss.data.database.shoppinglist.ShopIngredient
import com.javainiai.chefskiss.data.database.shoppinglist.ShopRecipe
import com.javainiai.chefskiss.data.database.shoppinglist.ShoppingListDao
import com.javainiai.chefskiss.data.database.tag.Tag
import com.javainiai.chefskiss.data.database.tag.TagDao

class Converters {
    @TypeConverter
    fun fromString(value: String?): Uri? {
        return if (value == null) null else Uri.parse(value)
    }

    @TypeConverter
    fun toString(uri: Uri?): String? {
        return uri?.toString()
    }
}


@Database(
    entities = [Recipe::class, PlannerRecipe::class, ShopRecipe::class, ShopIngredient::class, Ingredient::class, Tag::class, RecipeTagCrossRef::class, Config::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun RecipeDao(): RecipeDao
    abstract fun IngredientDao(): IngredientDao
    abstract fun TagDao(): TagDao
    abstract fun ConfigDao(): ConfigDao
    abstract fun ShoppingListDao(): ShoppingListDao
    abstract fun PlannerDao(): PlannerDao

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