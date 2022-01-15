package ru.maxim.barybians.data.repository

import dagger.Reusable
import ru.maxim.barybians.data.network.service.PostService
import javax.inject.Inject

@Reusable
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
) : PostRepository {

    override suspend fun getFeed() = repositoryBoundResource(postService::getFeed)

    override suspend fun createPost(title: String?, text: String) =
        repositoryBoundResource { postService.createPost(title, text) }

    override suspend fun updatePost(postId: Int, title: String?, text: String) =
        repositoryBoundResource { postService.updatePost(postId, title, text) }

    override suspend fun deletePost(postId: Int): Boolean =
        repositoryBoundResource { postService.deletePost(postId) }

    override suspend fun setLike(postId: Int) =
        repositoryBoundResource { postService.setLike(postId) }

    override suspend fun removeLike(postId: Int) =
        repositoryBoundResource { postService.removeLike(postId) }
}