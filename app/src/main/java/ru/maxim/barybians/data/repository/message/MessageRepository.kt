package ru.maxim.barybians.data.repository.message

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.network.model.ParseMode
import ru.maxim.barybians.domain.model.Message

interface MessageRepository {
    fun getMessagesPager(userId: Int): Flow<PagingData<Message>>
    fun getMessagesCount(userId: Int): Flow<Int>
    suspend fun sendMessage(uuid: String, text: String, parseMode: ParseMode)

    companion object {
        const val pageSize = 100
        const val prefetchDistance = 20
    }
}