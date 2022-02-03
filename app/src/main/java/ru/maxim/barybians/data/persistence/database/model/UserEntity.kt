package ru.maxim.barybians.data.persistence.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.maxim.barybians.data.persistence.database.model.UserEntity.Contract.tableName

@Entity(tableName = tableName)
data class UserEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = Columns.id)
    val id: Int,

    @ColumnInfo(name = Columns.firstName)
    val firstName: String,

    @ColumnInfo(name = Columns.lastName)
    val lastName: String,

    @ColumnInfo(name = Columns.photo)
    val photo: String?,

    @ColumnInfo(name = Columns.status)
    val status: String?,

    @ColumnInfo(name = Columns.birthDate)
    val birthDate: Long,

    @ColumnInfo(name = Columns.sex)
    val sex: String,

    @ColumnInfo(name = Columns.lastVisit)
    val lastVisit: Long,

    @ColumnInfo(name = Columns.roleId)
    val roleId: Int?,
) {
    companion object Contract {
        const val tableName = "users"

        object Columns {
            const val id = "id"
            const val firstName = "first_name"
            const val lastName = "lastName"
            const val photo = "photo"
            const val status = "status"
            const val birthDate = "birth_date"
            const val sex = "sex"
            const val lastVisit = "last_visit"
            const val roleId = "role_id"
        }
    }
}