package com.javainiai.chefskiss.data.database.services.configservice

import com.javainiai.chefskiss.data.database.config.Config
import com.javainiai.chefskiss.data.database.config.ConfigRepository

class ConfigServiceImpl(private val configRepository: ConfigRepository) : ConfigService {
    override suspend fun insert(config: Config) {
        configRepository.insert(config)
    }

    override suspend fun delete(config: Config) {
        configRepository.delete(config)
    }

    override fun getValue(key: String): String? {
        return configRepository.getValue(key)
    }
}