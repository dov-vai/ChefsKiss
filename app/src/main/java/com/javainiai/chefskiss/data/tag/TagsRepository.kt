package com.javainiai.chefskiss.data.tag

import kotlinx.coroutines.flow.Flow

interface TagsRepository {
    fun getAllTagsStream(): Flow<List<Tag>>
    fun getTagStream(id: Int): Flow<Tag?>
    fun getAllRecipeTags(recipeId: Int): Flow<List<Tag>>
    suspend fun insertTag(tag: Tag)
    suspend fun deleteTag(tag: Tag)
    suspend fun updateTag(tag: Tag)
}