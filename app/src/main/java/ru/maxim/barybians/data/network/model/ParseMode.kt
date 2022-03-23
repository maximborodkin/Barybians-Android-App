package ru.maxim.barybians.data.network.model

enum class ParseMode(val headerValue: String) {
    HTML("html"),
    BB("bb"),
    MD("md")
}