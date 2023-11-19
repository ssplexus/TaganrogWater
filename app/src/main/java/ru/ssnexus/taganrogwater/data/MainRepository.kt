package ru.ssnexus.taganrogwater.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import java.text.ParseException
import java.text.SimpleDateFormat
import kotlin.collections.ArrayList

class MainRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsDataObservable() : Observable<List<NotificationsData>> = notificationDao.getCachedDataObservable()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun putToDb(list: ArrayList<String>) {
        val notificationsCached = notificationDao.getCachedData() as ArrayList<NotificationsData>
//        val notificationsUpdateList = notificationsCached
        val newNotificationsList = ArrayList<NotificationsData>()

        list.forEach {
                val srcText = it.trim()
                val words = srcText.split(" ") as ArrayList<String>
                if(!words.isEmpty() && words.size > 1){
                    var ignore: Boolean = false
                    var date = words.removeFirst()
                    val formatter = SimpleDateFormat("dd.MM.yy")
                    try {
                        val dateFromNotif = formatter.parse(date)
                        dateFromNotif?.let { it_date ->
                            date = formatter.format(it_date)
                           }
                    } catch (e:ParseException){
                        e.printStackTrace()
                    }

                    val dateLst = date.split(".")
                    if(dateLst.size == 3)
                        date = dateLst[0] + "." + dateLst[1] + ".20" + dateLst[2]
                    val notification = words.joinToString(" ")

                    val iterator = notificationsCached.iterator()
                    while (iterator.hasNext()){
                        val notifItem = iterator.next()
                        if(date == notifItem.date && notification == notifItem.notifiction){
                            ignore = true
                            iterator.remove()
                            continue
                        }
                    }

                    if (!ignore)
                        newNotificationsList.add(NotificationsData(date = date, notifiction = notification))
                }
        }

        val iterator = notificationsCached.iterator()
        while (iterator.hasNext()) {
            iterator.next().marked = 0
        }
        notificationsCached.addAll(newNotificationsList)
        if(!notificationsCached.isEmpty())
            notificationDao.insertAll(notificationsCached)

    }

    fun updateMarkedStateById(id : Int) {
        notificationDao.updateMarkedById(id)
    }

    fun getMarkedStateById(id: Int) = notificationDao.getMarkedStateById(id)

    fun removeNotificationById(id: Int){
        notificationDao.removeNotificationById(id)
    }

    fun clearData(){
        notificationDao.nukeData()
    }

    fun getRowCount() = notificationDao.getSize()
}