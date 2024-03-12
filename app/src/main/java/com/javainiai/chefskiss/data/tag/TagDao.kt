package com.javainiai.chefskiss.data.tag

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Tag): Long

    @Update
    suspend fun update(item: Tag)

    @Delete
    suspend fun delete(item: Tag)

    @Query("SELECT * from tags WHERE id = :id")
    fun getTag(id: Long): Flow<Tag>

    @Query("SELECT * from tags ORDER BY title ASC")
    fun getAllTags(): Flow<List<Tag>>

    @Transaction
    @Query("SELECT * from tags WHERE id = :id")
    fun getTagWithRecipes(id: Long): Flow<TagWithRecipes>
}