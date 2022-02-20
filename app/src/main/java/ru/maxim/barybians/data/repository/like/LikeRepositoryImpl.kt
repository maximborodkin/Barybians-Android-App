package ru.maxim.barybians.data.repository.like

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.mapper.UserEntityMapper
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.User
import ru.maxim.barybians.utils.isNotNull
import javax.inject.Inject

class LikeRepositoryImpl @Inject constructor(
    private val likeDao: LikeDao,
    private val userEntityMapper: UserEntityMapper,
    private val preferencesManager: PreferencesManager,
    private val postService: PostService,
    private val repositoryBound: RepositoryBound
) : LikeRepository {

    override fun getLikes(postId: Int): Flow<List<User>> {
        return likeDao.getByPostId(postId).map { likesList ->
            userEntityMapper.toDomainModelList(likesList)
        }
    }

    override suspend fun changeLike(postId: Int) {
        val hasPersonalLike = likeDao.getLike(postId, preferencesManager.userId).isNotNull()

        val likeResponse = repositoryBound.wrapRequest {
            if (hasPersonalLike) postService.removeLike(postId)
            else postService.setLike(postId)
        }

        val likeEntities = likeResponse.whoLiked.map { user -> LikeEntity(postId = postId, userId = user.userId) }
        likeDao.deleteByPostId(postId)
        likeDao.insert(likeEntities)
    }
}