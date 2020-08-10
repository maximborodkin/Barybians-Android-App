package ru.maxim.barybians.repository.remote.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.response.CommentResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface CommentService {

    @FormUrlEncoded
    @POST("/api/comments")
    suspend fun addComment(@Field("postId") postId: Int,
                           @Field("text") text: String): Response<CommentResponse>

    @FormUrlEncoded
    @DELETE("/api/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): Response<String>

    companion object{
        operator fun invoke(): CommentService =
            RetrofitClient.instance.create(CommentService::class.java)
    }
}