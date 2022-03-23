package ru.maxim.barybians.data.network.service

import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.CommentDto

interface CommentService {

    @FormUrlEncoded
    @POST("/v2/comments")
    suspend fun addComment(
        @Header("request") uuid: String,
        @Field("postId") postId: Int,
        @Field("text") text: String,
        @Header("Parse-mode") parseMode: String
    ): Response<CommentDto>

    @FormUrlEncoded
    @Headers("Parse-mode: md")
    @PUT("/v2/comments/{commentId}")
    suspend fun editComment(
        @Path("commentId") commentId: Int,
        @Field("text") text: String,
        @Header("Parse-mode") parseMode: String
    ): Response<CommentDto>

    @DELETE("/v2/comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): Response<Boolean>
}