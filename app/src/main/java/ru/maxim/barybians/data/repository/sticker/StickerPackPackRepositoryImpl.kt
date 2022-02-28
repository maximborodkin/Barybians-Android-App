package ru.maxim.barybians.data.repository.sticker

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import ru.maxim.barybians.data.database.dao.StickerPackDao
import ru.maxim.barybians.data.database.model.mapper.StickerPackEntityMapper
import ru.maxim.barybians.data.network.model.mapper.StickerPackDtoMapper
import ru.maxim.barybians.data.network.service.StickerPackService
import ru.maxim.barybians.data.repository.RepositoryBound
import ru.maxim.barybians.domain.model.StickerPack
import javax.inject.Inject

class StickerPackPackRepositoryImpl @Inject constructor(
    private val repositoryBound: RepositoryBound,
    private val stickerPackService: StickerPackService,
    private val stickerPackDtoMapper: StickerPackDtoMapper,
    private val stickerPackDao: StickerPackDao,
    private val stickerPackEntityMapper: StickerPackEntityMapper,
) : StickerPackRepository {

    override suspend fun getStickerPacks(): Flow<List<StickerPack>> {
        val stickerPacks = stickerPackDao.getAll().firstOrNull()
        if (stickerPacks == null || stickerPacks.isEmpty()) {
            fetchStickerPacks()
        }
        return stickerPackDao.getAll().map { entity -> stickerPackEntityMapper.toDomainModelList(entity) }
    }

    private suspend fun fetchStickerPacks() {
        val stickerPacksDto = repositoryBound.wrapRequest { stickerPackService.getStickerPacks() }
        val stickerPacks = stickerPackDtoMapper.toDomainModelList(stickerPacksDto)
        stickerPackDao.insert(stickerPackEntityMapper.fromDomainModelList(stickerPacks))
    }
}