package ru.maxim.barybians.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import dagger.Reusable
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import ru.maxim.barybians.data.database.BarybiansDatabase
import ru.maxim.barybians.data.database.dao.CommentDao
import ru.maxim.barybians.data.database.model.CommentEntity
import ru.maxim.barybians.data.database.model.mapper.CommentEntityMapper
import ru.maxim.barybians.data.repository.CommentRepository
import ru.maxim.barybians.utils.transform

@Reusable
@OptIn(ExperimentalPagingApi::class)
class CommentRemoteMediator private constructor(
    private val commentRepository: CommentRepository,
    private val database: BarybiansDatabase,
    private val commentEntityMapper: CommentEntityMapper,
    private val commentDao: CommentDao,
    private val postId: Int
) : RemoteMediator<Int, CommentEntity>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, CommentEntity>): MediatorResult {
        return try {
            val page: Int = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val last = state.lastItemOrNull() ?: return MediatorResult.Success(endOfPaginationReached = false)
                    last.nextKey ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val commentsResponse = commentRepository.loadCommentsPage(
                postId = postId,
                startIndex = page * state.config.pageSize,
                count = state.config.pageSize
            )

            val prevKey = if (page == 0) null else page - 1
            val nextKey = if (commentsResponse.size < state.config.pageSize) null else page + 1

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    commentDao.deleteByPostId(postId)
                }

                val entities = commentEntityMapper.fromDomainModelList(commentsResponse)
                    .transform { comment -> comment.prevKey = prevKey; comment.nextKey = nextKey }

                commentDao.insert(entities)
            }
            MediatorResult.Success(endOfPaginationReached = commentsResponse.size < state.config.pageSize)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    class CommentsRemoteMediatorFactory @AssistedInject constructor(
        private val commentRepository: CommentRepository,
        private val database: BarybiansDatabase,
        private val commentEntityMapper: CommentEntityMapper,
        private val commentDao: CommentDao,
        @Assisted("postId") private val postId: Int
    ) {

        fun create() = CommentRemoteMediator(commentRepository, database, commentEntityMapper, commentDao, postId)

        @AssistedFactory
        interface Factory {
            fun provideFactory(@Assisted("postId") postId: Int): CommentsRemoteMediatorFactory
        }
    }
}