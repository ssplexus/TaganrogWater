package ru.ssnexus.taganrogwater.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao

@Database(entities = [NotificationsData::class], version = AppConstants.DB_VERSION, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun notificationDao(): NotificationDao
}

