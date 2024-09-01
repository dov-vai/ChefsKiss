package com.javainiai.chefskiss.data.database.tag

import kotlinx.coroutines.flow.Flow

class OfflineTagsRepository(private val tagDao: TagDao) :
    com.javainiai.chefskiss.data.database.tag.TagsRepository {
    override fun getAllTagsStream(): Flow<List<Tag>> = tagDao.getAllTags()
    override fun getTagStream(id: Long): Flow<Tag?> = tagDao.getTag(id)
    override suspend fun insertTag(tag: Tag): Long = tagDao.insert(tag)
    override suspend fun deleteTag(tag: Tag) = tagDao.delete(tag)
    override suspend fun updateTag(tag: Tag) = tagDao.update(tag)
}