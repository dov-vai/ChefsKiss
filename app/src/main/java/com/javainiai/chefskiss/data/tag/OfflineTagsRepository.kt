package com.javainiai.chefskiss.data.tag

import kotlinx.coroutines.flow.Flow

class OfflineTagsRepository(private val tagDao: TagDao) : TagsRepository {
    override fun getAllTagsStream(): Flow<List<Tag>> = tagDao.getAllTags()
    override fun getTagStream(id: Long): Flow<Tag?> = tagDao.getTag(id)
    override suspend fun insertTag(tag: Tag): Long = tagDao.insert(tag)
    override suspend fun deleteTag(tag: Tag) = tagDao.delete(tag)
    override suspend fun updateTag(tag: Tag) = tagDao.update(tag)
}