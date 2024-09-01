package com.javainiai.chefskiss.data.database.tag

import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    fun getAllTagsStream(): Flow<List<Tag>>
    fun getTagStream(id: Long): Flow<Tag?>
    suspend fun insertTag(tag: Tag): Long
    suspend fun deleteTag(tag: Tag)
    suspend fun updateTag(tag: Tag)
}