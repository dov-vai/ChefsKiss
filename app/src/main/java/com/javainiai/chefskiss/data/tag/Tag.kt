package com.javainiai.chefskiss.data.tag

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tags")
data class Tag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String
)