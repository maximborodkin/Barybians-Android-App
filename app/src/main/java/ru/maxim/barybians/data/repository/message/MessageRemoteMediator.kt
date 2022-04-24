package ru.maxim.barybians.data.repository.message

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dagger.Reusable
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.BarybiansDatabase
import ru.maxim.barybians.data.database.dao.AttachmentDao
import ru.maxim.barybians.data.database.dao.MessageAttachmentDao
import ru.maxim.barybians.data.database.dao.MessageDao
import ru.maxim.barybians.data.database.model.MessageEntity
import ru.maxim.barybians.data.database.model.mapper.MessageEntityMapper
import ru.maxim.barybians.data.network.model.mapper.MessageDtoMapper
import ru.maxim.barybians.data.network.service.MessageService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.utils.transform
import timber.log.Timber
import javax.inject.Inject

@Reusable
@OptIn(ExperimentalPagingApi::class)
class MessageRemoteMediator private constructor(
    private val userId: Int,
    private val preferencesManager: PreferencesManager,
    private val database: BarybiansDatabase,
    private val repositoryBound: RepositoryBound,
    private val messageEntityMapper: MessageEntityMapper,
    private val messageDtoMapper: MessageDtoMapper,
    private val messageService: MessageService,
    private val attachmentDao: AttachmentDao,
    private val messageAttachmentDao: MessageAttachmentDao,
    private val messageDao: MessageDao
) : RemoteMediator<Int, MessageEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, MessageEntity>): MediatorResult {
        Timber.d("MessageRemoteMediator load $loadType")
        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> {
                    val last = state.firstItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = false)
                    last.message.prevPage ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            }

            val startIndex = page * state.config.pageSize
            val count = state.config.pageSize
            Timber.d("MessageRemoteMediator start $startIndex count $count")
            val messagesDto = repositoryBound.wrapRequest {
                messageService.loadMessagesPage(userId = userId, startIndex = startIndex, count = count)
            }.messages
            Timber.d("MessageRemoteMediator loaded ${messagesDto.size} first: ${messagesDto.first().messageId} last: ${messagesDto.last().messageId}")
            val messagesPageResponse = messageDtoMapper.toDomainModelList(messagesDto)

            val prevPage = if (messagesPageResponse.size < state.config.pageSize) null else page + 1
            val nextPage = if (page == 0) null else page - 1

            val entities = messageEntityMapper.fromDomainModelList(messagesPageResponse).transform { message ->
                message.message.prevPage = prevPage; message.message.nextPage = nextPage
            }

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    messageDao.clear(userId, preferencesManager.userId)
                }
                messageDao.save(
                    messageEntities = entities,
                    attachmentDao = attachmentDao,
                    messageAttachmentDao = messageAttachmentDao
                )
            }
            MediatorResult.Success(endOfPaginationReached = messagesPageResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            Timber.e(e)
            MediatorResult.Error(e)
        }
    }

    class MessageRemoteMediatorFactory @Inject constructor(
        private val preferencesManager: PreferencesManager,
        private val database: BarybiansDatabase,
        private val repositoryBound: RepositoryBound,
        private val messageEntityMapper: MessageEntityMapper,
        private val messageDtoMapper: MessageDtoMapper,
        private val messageService: MessageService,
        private val attachmentDao: AttachmentDao,
        private val messageAttachmentDao: MessageAttachmentDao,
        private val messageDao: MessageDao
    ) {
        fun create(userId: Int) = MessageRemoteMediator(
            userId = userId,
            preferencesManager = preferencesManager,
            database = database,
            repositoryBound = repositoryBound,
            messageEntityMapper = messageEntityMapper,
            messageDtoMapper = messageDtoMapper,
            messageService = messageService,
            attachmentDao = attachmentDao,
            messageAttachmentDao = messageAttachmentDao,
            messageDao = messageDao
        )
    }
}