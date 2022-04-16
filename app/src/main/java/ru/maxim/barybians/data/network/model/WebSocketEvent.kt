package ru.maxim.barybians.data.network.model

data class WebSocketEvent(
    val event: String,
    val data: Any
)