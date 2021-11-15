package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.response.CommentResponse

interface CommentService {

    @FormUrlEncoded
    @POST("/api/comments")
    suspend fun addComment(@Field("postId") postId: Int,
                           @Field("text") text: String): Response<CommentResponse>
    
    @DELETE("/api/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): Response<String>
}