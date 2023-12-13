package ru.ssnexus.taganrogwater.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class MainRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsDataObservable() : Observable<List<NotificationsData>> = notificationDao.getCachedDataObservable()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun putToDb(list: ArrayList<String>) {
        val notificationsCached = notificationDao.getCachedData() as ArrayList<NotificationsData>
        val cacheSize = notificationsCached.size
        var newId = cacheSize
        val newNotificationsList = ArrayList<NotificationsData>()

        list.forEach {
                val srcText = it.trim()
                val words = srcText.split(" ") as ArrayList<String>
                if(!words.isEmpty() && words.size > 1){
                    var ignore: Boolean = false
                    var dateStr = words.removeFirst()
                    val formatter = SimpleDateFormat("dd.MM.yy")
                    try {
                        val dateFromNotif = formatter.parse(dateStr)
                        dateFromNotif?.let { it_date ->
                            dateStr = formatter.format(it_date)
                           }

                        val dateLst = dateStr.split(".")
                        if(dateLst.size == 3) {
                            dateStr = dateLst[0] + "." + dateLst[1] + ".20" + dateLst[2]

                            val notification = words.joinToString(" ")
                            val date = formatter.parse(dateStr) as Date

                            val iterator = notificationsCached.iterator()
                            while (iterator.hasNext()){
                                val notifItem = iterator.next()
                                if(date.time == notifItem.date && notification == notifItem.notifiction){
                                    ignore = true
                                    iterator.remove()
                                    continue
                                }
                            }

                            if (!ignore)
                                newNotificationsList.add(NotificationsData(id = newId++, date = date.time, notifiction = notification))
                        }
                    } catch (e:ParseException){
                        e.printStackTrace()
                    }
                }
        }

        val iterator = notificationsCached.iterator()
        while (iterator.hasNext()) {
            iterator.next().marked = 0
        }

        if(cacheSize != 0) {
            val formatter = SimpleDateFormat("dd.MM.yyyy")
            newNotificationsList.forEach {
                try {
                    val dateFromNotif = formatter.format(it.date)
                    NotificationHelper.createNotification(
                        App.instance.applicationContext,
                        it.id,
                        dateFromNotif,
                        it.notifiction
                    )
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        }

        notificationsCached.addAll(newNotificationsList)
        if(!notificationsCached.isEmpty())
            notificationDao.insertAll(notificationsCached)

    }

    fun updateMarkedStateById(id : Int) {
        notificationDao.updateMarkedById(id)
    }

    fun setMarkedStateById(id : Int, value: Int) {
        notificationDao.setMarkedById(id, value)
    }

    fun getMarkedStateById(id: Int) = notificationDao.getMarkedStateById(id)

    fun removeNotificationById(id: Int){
        notificationDao.removeNotificationById(id)
    }

    fun removeArchiveData(){
        notificationDao.removeArchiveData()
    }

    fun clearData(){
        notificationDao.nukeData()
    }

    fun getRowCount() = notificationDao.getSize()

    fun checkNotificationId(id: Int) = notificationDao.checkId(id)
}