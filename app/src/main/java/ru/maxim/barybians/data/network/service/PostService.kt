package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.response.LikeResponse
import ru.maxim.barybians.domain.model.Post

interface PostService {

    @GET("/v2/posts/{postId}")
    suspend fun getById(@Path("postId") postId: Int): Response<Post?>

    @GET("/v2/posts")
    suspend fun getFeed(): Response<List<Post>>

    @FormUrlEncoded
    @POST("/v2/posts")
    suspend fun createPost(
        @Field("title") title: String?,
        @Field("text") text: String
    ): Response<Post>

    @FormUrlEncoded
    @PUT("/v2/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Int,
        @Field("title") title: String?,
        @Field("text") text: String
    ): Response<Post>

    @DELETE("/v2/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int): Response<Boolean>

    @POST("/v2/posts/{postId}/like")
    suspend fun setLike(@Path("postId") postId: Int): Response<LikeResponse>

    @DELETE("/v2/posts/{postId}/like")
    suspend fun removeLike(@Path("postId") postId: Int): Response<LikeResponse>
}