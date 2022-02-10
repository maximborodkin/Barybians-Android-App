package ru.maxim.barybians.data.persistence.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "post_paging_keys")
data class PostPagingKey(
    @PrimaryKey(autoGenerate = false)
    val postId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)