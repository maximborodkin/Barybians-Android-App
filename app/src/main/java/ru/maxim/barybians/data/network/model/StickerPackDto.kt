package ru.maxim.barybians.data.network.model

import com.google.gson.annotations.SerializedName

data class StickerPackDto(
    @SerializedName("Name")
    val name: String,
    @SerializedName("Pack")
    val pack: String,
    @SerializedName("Icon")
    val icon: String
)
