package ru.maxim.barybians.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.database.model.StickerPackEntity.Contract.tableName

@Entity(tableName = tableName)
class StickerPackEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Columns.name)
    val name: String,

    @ColumnInfo(name = Columns.pack)
    val pack: String,

    @ColumnInfo(name = Columns.icon)
    val icon: String,

    @ColumnInfo(name = Columns.amount)
    val amount: Int
) {
    companion object Contract {
        const val tableName = "sticker_packs"

        object Columns {
            const val name = "name"
            const val pack = "pack"
            const val icon = "icon"
            const val amount = "amount"
        }
    }
}