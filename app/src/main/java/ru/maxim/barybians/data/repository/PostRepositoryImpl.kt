package ru.maxim.barybians.data.repository

import dagger.Reusable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.maxim.barybians.data.network.RetrofitClient
import ru.maxim.barybians.data.network.service.PostService
import ru.maxim.barybians.domain.model.Post
import javax.inject.Inject

@Reusable
class PostRepositoryImpl @Inject constructor(
    private val postService: PostService,
    private val retrofitClient: RetrofitClient
) : PostRepository {

    override fun getFeed(): Flow<Result<List<Post>>> = repositoryBoundResource(
        databaseQuery = { emptyFlow() },
        networkRequest = postService::getFeed,
        cacheResponse = { response ->
            TODO("Cahce response")
        }
    )

    override fun createPost(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun updatePost(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun deletePost(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun setLike(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun removeLike(): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

}