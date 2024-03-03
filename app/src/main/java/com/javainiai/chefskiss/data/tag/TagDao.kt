package com.javainiai.chefskiss.data.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Tag)

    @Update
    suspend fun update(item: Tag)

    @Delete
    suspend fun delete(item: Tag)

    @Query("SELECT * from tags WHERE id = :id")
    fun getTag(id: Int): Flow<Tag>

    @Query("SELECT * from tags ORDER BY title ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Query("SELECT * from tags WHERE recipeId = :recipeId")
    fun getAllRecipeTags(recipeId: Int): Flow<List<Tag>>
}