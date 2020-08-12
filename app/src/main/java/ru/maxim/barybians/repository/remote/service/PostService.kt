package ru.maxim.barybians.repository.remote.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.Post
import ru.maxim.barybians.model.response.LikeResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface PostService {

    @GET("/api/posts")
    suspend fun getFeed(): Response<ArrayList<Post>>

    @FormUrlEncoded
    @POST("/api/posts")
    suspend fun createPost(@Field("title") title: String?,
                           @Field("text") text: String): Response<Post>

    @FormUrlEncoded
    @PUT("/api/posts/{postId}")
    suspend fun updatePost(@Path("postId") postId: Int,
                   @Field("title") newText: String?,
                   @Field("text") newText1: String): Response<Post>

    @DELETE("/api/posts/{postId}")
    suspend fun deletePost(@Path("postId") postId: Int): Response<String>

    @POST("/api/posts/{postId}/like")
    suspend fun addLike(@Path("postId") postId: Int): Response<LikeResponse>

    @DELETE("/api/posts/{postId}/like")
    suspend fun removeLike(@Path("postId") postId: Int): Response<LikeResponse>

    companion object{
        operator fun invoke(): PostService =
            RetrofitClient.instance.create(PostService::class.java)
    }
}