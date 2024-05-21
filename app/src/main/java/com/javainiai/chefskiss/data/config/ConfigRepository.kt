package com.javainiai.chefskiss.data.config

interface ConfigRepository {
    suspend fun insert(config: Config)

    suspend fun delete(config: Config)

    fun getValue(key: String): String?
}