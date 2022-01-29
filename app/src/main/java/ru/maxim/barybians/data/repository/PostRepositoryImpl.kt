package ru.maxim.barybians.data.repository

import dagger.Reusable
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.domain.model.Post
import javax.inject.Inject

@Reusable
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val repositoryBound: RepositoryBound
) : PostRepository {

    override suspend fun getFeed() = repositoryBound.wrapRequest(postService::getFeed)

    override suspend fun getPostById(postId: Int): Post? =
        repositoryBound.wrapRequest { postService.getById(postId) }.firstOrNull { it.id == postId }

    override suspend fun createPost(title: String?, text: String) =
        repositoryBound.wrapRequest { postService.createPost(title, text) }

    override suspend fun updatePost(postId: Int, title: String?, text: String) =
        repositoryBound.wrapRequest { postService.updatePost(postId, title, text) }

    override suspend fun deletePost(postId: Int): Boolean =
        repositoryBound.wrapRequest { postService.deletePost(postId) }

    override suspend fun setLike(postId: Int) =
        repositoryBound.wrapRequest { postService.setLike(postId) }

    override suspend fun removeLike(postId: Int) =
        repositoryBound.wrapRequest { postService.removeLike(postId) }
}