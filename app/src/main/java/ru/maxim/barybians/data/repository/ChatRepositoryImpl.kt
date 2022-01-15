package ru.maxim.barybians.data.repository

import ru.maxim.barybians.data.network.service.ChatService
import ru.maxim.barybians.data.persistence.PreferencesManager
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatService: ChatService,
    private val preferencesManager: PreferencesManager
) : ChatRepository {

}