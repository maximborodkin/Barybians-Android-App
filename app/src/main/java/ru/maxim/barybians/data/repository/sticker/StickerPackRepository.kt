package ru.maxim.barybians.data.repository.sticker

import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.domain.model.StickerPack

interface StickerPackRepository {
    suspend fun getStickerPacks(): Flow<List<StickerPack>>
}