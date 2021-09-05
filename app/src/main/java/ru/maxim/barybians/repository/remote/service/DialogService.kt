package ru.maxim.barybians.repository.remote.service

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.*
import ru.maxim.barybians.model.Dialog
import ru.maxim.barybians.model.StickerPack
import ru.maxim.barybians.model.response.DialogResponse
import ru.maxim.barybians.model.response.SendMessageResponse
import ru.maxim.barybians.repository.remote.RetrofitClient

interface DialogService {

    /**
     * Method for getting list of dialogs.
     * @return list of ...
     **/
    @GET("/api/dialogs")
    suspend fun getDialogsList(): Response<ArrayList<Dialog>>

    /**
     * Method for getting all messages in dialog between current user and interlocutor
     * specified by [userId].
     * @param userId is id of interlocutor. @see [ru.maxim.barybians.model.User.id].
     * @return [DialogResponse] object.
     **/
    @GET("/api/dialogs/{userId}")
    suspend fun getMessages(@Path("userId") userId: Int): Response<DialogResponse>

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
     * Used in [ru.maxim.barybians.ui.activity.dialog.DialogPresenter].
     * Working by longpolling algorithm.
     * @param interlocutorId is id of interlocutor. @see [ru.maxim.barybians.model.User.id].
     * @param lastMessageId is id of last received message from any interlocutor.
     * @return [DialogResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/api/v2/longpoll/messages/{interlocutorId}")
    suspend fun observeMessagesForUser(@Path("interlocutorId") interlocutorId: Int,
                                       @Query("last") lastMessageId: Int): Response<DialogResponse>

    /**
     * Method for observing new messages from all interlocutors for notify user by
     * status bar notification. @see [androidx.core.app.NotificationCompat].
     * Working by longpolling algorithm.
     * @param lastMessageId is id of last received message from any interlocutor.
     * @return [DialogResponse] or throws [java.util.concurrent.TimeoutException]
     * if no new messages in 50 seconds.
     **/
    @GET("/api/messages/lastMessage={lastMessageId}")
    suspend fun observeNewMessages(@Path("lastMessageId") lastMessageId: Int): Response<JsonObject>

    companion object {
        operator fun invoke(): DialogService =
            RetrofitClient.instance.create(DialogService::class.java)
    }
}