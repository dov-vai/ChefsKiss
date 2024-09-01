package com.javainiai.chefskiss.data.database.config

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "config")
data class Config(
    @PrimaryKey
    val key: String,
    val value: String
)