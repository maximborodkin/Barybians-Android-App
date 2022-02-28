package ru.maxim.barybians.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import ru.maxim.barybians.data.database.model.StickerPackEntity
import ru.maxim.barybians.data.database.model.StickerPackEntity.Contract.Columns

@Dao
interface StickerPackDao {

    @Query("SELECT * FROM ${StickerPackEntity.tableName}")
    fun getAll(): Flow<List<StickerPackEntity>>

    @Query("SELECT * FROM ${StickerPackEntity.tableName} WHERE ${Columns.name}=:name")
    fun getByName(name: String): Flow<StickerPackEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stickerPackEntities: List<StickerPackEntity>)
}