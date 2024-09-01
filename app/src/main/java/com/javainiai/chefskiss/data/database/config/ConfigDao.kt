package com.javainiai.chefskiss.data.database.config

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ConfigDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun Insert(item: Config)

    @Delete
    suspend fun Delete(item: Config)

    @Query("SELECT value FROM config WHERE key = :key")
    fun Get(key: String): String?
}