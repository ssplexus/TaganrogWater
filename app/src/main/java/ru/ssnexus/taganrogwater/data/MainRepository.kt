package ru.ssnexus.taganrogwater.data

import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import timber.log.Timber

class MainRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsDataObservable() : Observable<List<NotificationsData>> = notificationDao.getCachedDataObservable()

    fun putToDb(list: ArrayList<String>) {
        val notificationsData = ArrayList<NotificationsData>()
        list.forEach {
                val srcText = it.trim()
                var words = srcText.split(" ") as ArrayList<String>
                if(!words.isEmpty() && words.size > 1)
                    notificationsData.add(NotificationsData(date = words.removeFirst(), notifiction = words.joinToString(" ")))
        }
        Timber.d("notificationsData.size = " + notificationsData.size)
        notificationDao.insertAll(notificationsData)
    }

    fun updateTrackListenLaterStateById(id : Int) {
        notificationDao.updateMarkedById(id)
    }

    fun clearData(){
        notificationDao.nukeData()
    }

    fun getRowCount() = notificationDao.getSize()
}