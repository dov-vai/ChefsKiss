package com.javainiai.chefskiss.data.database.services.configservice

import com.javainiai.chefskiss.data.database.config.Config

interface ConfigService {
    suspend fun insert(config: Config)

    suspend fun delete(config: Config)

    fun getValue(key: String): String?
}