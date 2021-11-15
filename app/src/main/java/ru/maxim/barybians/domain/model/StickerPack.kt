package ru.maxim.barybians.domain.model

import com.google.gson.annotations.SerializedName

data class StickerPack (
    @SerializedName("Name")
    val name: String,
    @SerializedName("Pack")
    val pack: String,
    @SerializedName("Icon")
    val icon: String
)
