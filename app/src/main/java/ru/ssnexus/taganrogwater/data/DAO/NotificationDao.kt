package ru.ssnexus.taganrogwater.data.DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.taganrogwater.data.entity.NotificationsData

//Помечаем, что это не просто интерфейс, а Dao-объект
@Dao
interface NotificationDao {
    @Query("SELECT * FROM cached_data")
    fun getCachedDataObservable(): Observable<List<NotificationsData>>

    @Query("SELECT * FROM cached_data")
    fun getCachedData(): List<NotificationsData>

    //Кладём списком в БД, в случае конфликта перезаписываем
    @Insert(entity = NotificationsData::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<NotificationsData>)

    // Снять/убрать пометку
    @Query("UPDATE cached_data SET marked = marked * (-1) WHERE id = :id")
    fun updateMarkedById(id : Int)

    // Получить состояние пометки уведомления
    @Query("SELECT marked FROM cached_data WHERE id = :id")
    fun getMarkedStateById(id : Int): Int

    // Очистка таблицы
    @Query("DELETE FROM cached_data")
    fun nukeData()

    // Очистка таблицы
    @Query("DELETE FROM cached_data WHERE id = :id")
    fun removeNotificationById(id: Int)

    @Query("SELECT COUNT(*) FROM cached_data")
    fun getSize(): Int
}
