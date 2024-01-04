package ru.ssnexus.taganrogwater.data.entity
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "cached_data", indices = [Index(value = ["id"], unique = true)])
data class NotificationsData(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    @ColumnInfo(name = "marked") var marked:Int = -1,
    @ColumnInfo(name = "date") val date: Long = 0,
    @ColumnInfo(name = "notifiction") val notifiction: String
):Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as NotificationsData

        if (date != other.date) return false
        if (notifiction != other.notifiction) return false

        return true
    }

    override fun hashCode(): Int {
        var result = date.hashCode()
        result = 31 * result + notifiction.hashCode()
        return result
    }
}
