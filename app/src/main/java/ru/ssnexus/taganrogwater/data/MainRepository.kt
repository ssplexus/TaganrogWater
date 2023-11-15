package ru.ssnexus.taganrogwater.data

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.core.Observable
import okhttp3.internal.notify
import ru.ssnexus.taganrogwater.data.DAO.NotificationDao
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class MainRepository(private val notificationDao: NotificationDao) {
    fun getNotificationsDataObservable() : Observable<List<NotificationsData>> = notificationDao.getCachedDataObservable()

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    fun putToDb(list: ArrayList<String>) {
        val notificationsData = ArrayList<NotificationsData>()
        val notificationsCached = notificationDao.getCachedData() as ArrayList<NotificationsData>

        list.forEach {
                val srcText = it.trim()
                var words = srcText.split(" ") as ArrayList<String>
                if(!words.isEmpty() && words.size > 1){
                    var ignore: Boolean = false
                    var marked = 0
                    var date = words.removeFirst()
                    val formatter = SimpleDateFormat("dd.MM.yy")
                    try {
                        val dateFromNotif = formatter.parse(date)
                        if (dateFromNotif != null) if(dateFromNotif < Date()) marked = -1

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
                    notificationsCached.forEach{
                        if(date == it.date && notification == it.notifiction) {
                            ignore = true
                            return@forEach
                        }
                    }
                    if(!ignore)
                        notificationsData.add(NotificationsData(marked = marked, date = date, notifiction = notification))
                }
        }
        Timber.d("notificationsData.size = " + notificationsData.size)
        if(!notificationsData.isEmpty()) notificationDao.insertAll(notificationsData)
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