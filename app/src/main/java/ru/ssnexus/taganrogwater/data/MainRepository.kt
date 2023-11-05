package ru.ssnexus.taganrogwater.data

import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao
import ru.ssnexus.taganrogwater.data.entity.NotificationsData

class MainRepository(private val notificationDao: NotificationDao) {
    fun getTracksDataObservable() : Observable<List<NotificationsData>> = notificationDao.getCachedDataObservable()
    fun putToDb(list: List<NotificationsData>) {

    }

    fun updateTrackListenLaterStateById(id : Int) {
        notificationDao.updateMarkedById(id)
    }

    fun clearData(){
        notificationDao.nukeData()
    }

    fun getRowCount() = notificationDao.getSize()
}