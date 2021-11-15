package ru.maxim.barybians.data.network.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.domain.model.Chat
import ru.maxim.barybians.domain.model.StickerPack
import ru.maxim.barybians.data.network.response.ChatResponse
import ru.maxim.barybians.data.network.response.SendMessageResponse

interface ChatService {

    /**
     * Method for getting list of chats.
     * @return list of ...
     **/
    @GET("/api/dialogs")
    suspend fun getChatsList(): Response<ArrayList<Chat>>

    /**
     * Method for getting all messages in chat between current user and interlocutor
     * specified by [userId].
     * @param userId is id of interlocutor. @see [ru.maxim.barybians.model.User.id].
     * @return [ChatResponse] object.
     **/
    @GET("/api/dialogs/{userId}")
    suspend fun getMessages(@Path("userId") userId: Int): Response<ChatResponse>

    /**
     * Method for sending a message to interlocutor specified by [userId].
     * @param userId is id of interlocutor. @see [ru.maxim.barybians.model.User.id].
     * @param text is body of message. Should not be empty.
     * @return string response. true if request successful, false otherwise.
     **/
    @FormUrlEncoded
    @POST("/api/dialogs/{userId}")
    suspend fun sendMessage(@Path("userId") userId: Int, @Field("text") text: String): Response<SendMessageResponse>

    /**
     * Method for getting a list of stickers packs.
     * @return list of [StickerPack] objects.
     **/
    @GET("/api/stickers")
    suspend fun getStickersPacks(): Response<ArrayList<StickerPack>>

    /**
     * Method for observing new messages from certain by [interlocutorId] user.
     * Used in [ru.maxim.barybians.ui.fragment.chat.ChatPresenter].
     * Working by longpolling algorithm.
     * @param interlocutorId is id of interlocutor. @see [ru.maxim.barybians.model.User.id].
     * @param lastMessageId is id of last received message from any interlocutor.
     * @return [ChatResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/api/v2/longpoll/messages/{interlocutorId}")
    suspend fun observeMessagesForUser(@Path("interlocutorId") interlocutorId: Int,
                                       @Query("last") lastMessageId: Int): Response<ChatResponse>

    /**
     * Method for observing new messages from all interlocutors for notify user by
     * status bar notification. @see [androidx.core.app.NotificationCompat].
     * Working by longpolling algorithm.
     * @param lastMessageId is id of last received message from any interlocutor.
     * @return [ChatResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/api/messages/lastMessage={lastMessageId}")
    suspend fun observeNewMessages(@Path("lastMessageId") lastMessageId: Int): Response<JsonObject>
}