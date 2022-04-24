package ru.maxim.barybians.data.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.model.mapper.MessageEntityMapper
import ru.maxim.barybians.data.network.model.ParseMode
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class MessageRepositoryImpl @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val messageDao: MessageDao,
    private val messageEntityMapper: MessageEntityMapper,
    private val messageRemoteMediatorFactory: MessageRemoteMediator.MessageRemoteMediatorFactory
) : MessageRepository {

    override fun getMessagesPager(userId: Int) = Pager(
        config = PagingConfig(
            initialLoadSize = MessageRepository.pageSize,
            pageSize = MessageRepository.pageSize,
            prefetchDistance = MessageRepository.prefetchDistance,
            enablePlaceholders = true
        ),
        remoteMediator = messageRemoteMediatorFactory.create(userId),
        pagingSourceFactory = { messageDao.messagesPagingSource(userId, preferencesManager.userId) }
    )
        .flow
        .map { pagingData -> pagingData.map { entityModel -> messageEntityMapper.toDomainModel(entityModel) } }

    override fun getMessagesCount(userId: Int): Flow<Int> = messageDao.messagesCount(userId, preferencesManager.userId)

    override suspend fun sendMessage(uuid: String, text: String, parseMode: ParseMode) = withContext(IO) {

    }
}