package ru.maxim.barybians.data.repository.post

import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import ru.maxim.barybians.data.PreferencesManager
import ru.maxim.barybians.data.database.dao.LikeDao
import ru.maxim.barybians.data.database.dao.PostDao
import ru.maxim.barybians.data.database.model.LikeEntity
import ru.maxim.barybians.data.database.model.PostEntity
import ru.maxim.barybians.data.database.model.mapper.PostEntityMapper
import ru.maxim.barybians.data.network.model.mapper.PostDtoMapper
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.Post
import ru.maxim.barybians.utils.isNotNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val postDao: PostDao,
    private val likeDao: LikeDao,
    private val postEntityMapper: PostEntityMapper,
    private val postDtoMapper: PostDtoMapper,
    private val repositoryBound: RepositoryBound,
) : PostRepository {

    override suspend fun loadFeedPage(startIndex: Int, count: Int): List<Post> {
        val postDto = repositoryBound.wrapRequest { postService.loadFeedPage(startIndex, count) }
        return postDtoMapper.toDomainModelList(postDto)
    }

    override fun feedPagingSource(): PagingSource<Int, PostEntity> {
        return postDao.feedPagingSource()
    }

    override suspend fun getPostById(postId: Int): Post? {
        val postDto = repositoryBound.wrapRequest { postService.getById(postId) } ?: return null
        return postDtoMapper.toDomainModel(postDto)
    }

    override suspend fun createPost(title: String?, text: String) = withContext(IO) {
        val postDto = repositoryBound.wrapRequest { postService.createPost(title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.insert(postEntityMapper.fromDomainModel(post).post)
    }

    override suspend fun editPost(postId: Int, title: String?, text: String) = withContext(IO)  {
        val postDto = repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }
        val post = postDtoMapper.toDomainModel(postDto)
        postDao.insert(postEntityMapper.fromDomainModel(post).post)
    }

    override suspend fun deletePost(postId: Int) = withContext(IO) {
        val isDeleted = repositoryBound.wrapRequest { postService.deletePost(postId) }
        if (isDeleted) {
            postDao.delete(postId)
        }
    }
}