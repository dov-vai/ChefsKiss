package com.javainiai.chefskiss.data.database.config

class OfflineConfigRepository(private val dao: ConfigDao) : ConfigRepository {
    override suspend fun insert(config: Config) = dao.Insert(config)

    override suspend fun delete(config: Config) = dao.Delete(config)

    override fun getValue(key: String): String? = dao.Get(key)
}