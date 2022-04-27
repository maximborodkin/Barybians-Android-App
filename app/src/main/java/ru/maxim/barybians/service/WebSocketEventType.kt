package ru.maxim.barybians.service

enum class WebSocketEventType(val serializedName: String) {
    MessageSent("message_sended"),
    MessageRead("message_readed")
}