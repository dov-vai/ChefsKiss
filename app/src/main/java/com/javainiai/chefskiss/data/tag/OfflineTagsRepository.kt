package com.javainiai.chefskiss.data.tag

import kotlinx.coroutines.flow.Flow

class OfflineTagsRepository(private val tagDao: TagDao) : TagsRepository {
    override fun getAllTagsStream(): Flow<List<Tag>> = tagDao.getAllTags()
    override fun getTagStream(id: Int): Flow<Tag?> = tagDao.getTag(id)
    override fun getAllRecipeTags(recipeId: Int): Flow<List<Tag>> =
        tagDao.getAllRecipeTags(recipeId)

    override suspend fun insertTag(tag: Tag) = tagDao.insert(tag)
    override suspend fun deleteTag(tag: Tag) = tagDao.delete(tag)
    override suspend fun updateTag(tag: Tag) = tagDao.update(tag)
}