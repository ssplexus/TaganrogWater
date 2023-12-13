package ru.ssnexus.taganrogwater.data.entity
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.sql.Date
import java.util.*

@Parcelize
@Entity(tableName = "cached_data", indices = [Index(value = ["id"], unique = true)])
data class NotificationsData(
    @PrimaryKey(autoGenerate = false) val id: Int = 0,
    @ColumnInfo(name = "marked") var marked:Int = -1,
    @ColumnInfo(name = "date") val date: Long = 0,
    @ColumnInfo(name = "notifiction") val notifiction: String
):Parcelable
