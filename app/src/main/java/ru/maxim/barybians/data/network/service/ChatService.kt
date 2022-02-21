package ru.maxim.barybians.data.network.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.data.network.model.MessageDto
import ru.maxim.barybians.data.network.model.StickerPackDto
import ru.maxim.barybians.data.network.model.response.MessagesResponse
import ru.maxim.barybians.domain.model.Chat

interface ChatService {

    /**
     * Method for getting list of chats.
     * @return list of chats
     **/
    @GET("/v2/dialogs")
    suspend fun getChatsList(): Response<ArrayList<Chat>>

    /**
     * Method for getting all messages in chat between current user and interlocutor
     * specified by [userId].
     * @param userId is postId of interlocutor. @see [ru.maxim.barybians.model.User.postId].
     * @return [ChatResponse] object.
     **/
    @GET("/v2/dialogs/{userId}")
    suspend fun getMessages(@Path("userId") userId: Int): Response<MessagesResponse>

    /**
     * Method for sending a message to interlocutor specified by [userId].
     * @param userId is postId of interlocutor. @see [ru.maxim.barybians.model.User.postId].
     * @param text is body of message. Should not be empty.
     * @return string response. true if request successful, false otherwise.
     **/
    @FormUrlEncoded
    @POST("/v2/dialogs/{userId}")
    suspend fun sendMessage(
        @Path("userId") userId: Int,
        @Field("text") text: String
    ): Response<MessageDto>

    /**
     * Method for getting a list of stickers packs.
     * @return list of [StickerPackDto] objects.
     **/
    @GET("/v2/stickers")
    suspend fun getStickersPacks(): Response<ArrayList<StickerPackDto>>

    /**
     * Method for observing new messages from certain by [interlocutorId] user.
     * Used in [ru.maxim.barybians.ui.fragment.chat.ChatPresenter].
     * Working by longpolling algorithm.
     * @param interlocutorId is postId of interlocutor. @see [ru.maxim.barybians.model.User.postId].
     * @param lastMessageId is postId of last received message from any interlocutor.
     * @return [ChatResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/v2/longpoll/messages/{interlocutorId}")
    suspend fun observeMessagesForUser(
        @Path("interlocutorId") interlocutorId: Int,
        @Query("last") lastMessageId: Int
    ): Response<MessagesResponse>

    /**
     * Method for observing new messages from all interlocutors for notify user by
     * status bar notification. @see [androidx.core.app.NotificationCompat].
     * Working by longpolling algorithm.
     * @param lastMessageId is postId of last received message from any interlocutor.
     * @return [ChatResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/v2/messages/lastMessage={lastMessageId}")
    suspend fun observeNewMessages(@Path("lastMessageId") lastMessageId: Int): Response<JsonObject>
}