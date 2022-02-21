package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.PostDto
import ru.maxim.barybians.data.network.model.response.LikeResponse
import ru.maxim.barybians.domain.model.Post

interface PostService {

    @GET("/v2/posts/{postId}")
    suspend fun getById(@Path("postId") postId: Int): Response<PostDto?>

    @GET("/v2/posts")
    suspend fun loadFeedPage(
        @Query("start") startIndex: Int,
        @Query("end") count: Int
    ): Response<List<PostDto>>

    @FormUrlEncoded
    @POST("/v2/posts")
    suspend fun createPost(
        @Field("title") title: String?,
        @Field("text") text: String
    ): Response<PostDto>

    @FormUrlEncoded
    @PUT("/v2/posts/{postId}")
    suspend fun updatePost(
        @Path("postId") postId: Int,
        @Field("title") title: String?,
        @Field("text") text: String
    ): Response<PostDto>

    @DELETE("/v2/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int): Response<Boolean>

    @POST("/v2/posts/{postId}/like")
    suspend fun setLike(@Path("postId") postId: Int): Response<LikeResponse>

    @DELETE("/v2/posts/{postId}/like")
    suspend fun removeLike(@Path("postId") postId: Int): Response<LikeResponse>
}