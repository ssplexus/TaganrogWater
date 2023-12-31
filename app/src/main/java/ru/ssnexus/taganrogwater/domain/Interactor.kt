package ru.ssnexus.taganrogwater.domain

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide.init
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import ru.ssnexus.taganrogwater.*
import ru.ssnexus.taganrogwater.activity.MainActivity
import ru.ssnexus.taganrogwater.data.MainRepository
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.preferences.PreferencesProvider
import ru.ssnexus.taganrogwater.receivers.NotificationReceiver
import ru.ssnexus.taganrogwater.utils.SingleLiveEvent
import ru.ssnexus.taganrogwater.utils.addTo
import timber.log.Timber
import java.net.URL

class Interactor(private val repo: MainRepository, private val prefs: PreferencesProvider) {

    private val notificationCachedList = ArrayList<NotificationsData>()
    private var notificationLiveData =  MutableLiveData <List<NotificationsData>>()
    private val checkDataResult = SingleLiveEvent<Boolean>()
    init {

    }

    fun initDataObservable(main: MainActivity){
            repo.getNotificationsDataObservable().subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    Timber.d("ERROR : NotificationsDataObservable Error!")
                    notificationCachedList.clear()
                    val emptyList = ArrayList<NotificationsData>()
                    notificationLiveData.postValue(emptyList)
                }.subscribe{
//                if(!it.isEmpty()) {
                    Timber.d("notificationLiveData.postValue(it)")
                    notificationCachedList.clear()
                    notificationCachedList.addAll(it)
                    notificationLiveData.postValue(it)
//                }
            }.addTo(main.autoDisposable)
    }

    fun getNotificationCachedList() = notificationCachedList

    fun getNotificationsListFromDB() = repo.getCachedData()

    fun getNotificationLiveData() = notificationLiveData

    fun getCheckDataResultLiveData() = checkDataResult

    fun updateMarkedStateById(id: Int){
        repo.updateMarkedStateById(id)
    }

    fun setMarkedStateById(id: Int, value: Int){
        repo.setMarkedStateById(id, value)
    }

    fun removeNotificationById(id: Int){
        repo.removeNotificationById(id)
    }

    fun getMarkedStateById(id: Int):Int {
        return repo.getMarkedStateById(id)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getData(){
        if(App.instance.interactor.getCheckDataPref()){
            // Create a new coroutine scope
            val scope = CoroutineScope(Dispatchers.Default)
            // Launch a new coroutine in the scope
            scope.launch {
                val url = URL(AppConstants.DATA_URL)
                try{
                    val doc: Document = Jsoup.connect(url.toString()).get()
                    var element = doc.select("table").get(1)
                    val rows = element.select("tr")
                    var notifications = ArrayList<String>()

                    rows.forEach{row ->
                        notifications.add(row.text())
                    }
                    notifications.removeLast()

                    if (!notifications.isEmpty()) repo.putToDb(notifications)
                    checkDataResult.postValue(true)
                }catch (e: Exception){
                    Timber.d(e.printStackTrace().toString())
                    checkDataResult.postValue(false)
                }
            }
        }
    }

    fun setShowArchivePref(flag: Boolean){
        prefs.setShowArchivePref(flag)
    }
    fun setShowNotifPref(flag: Boolean){
        prefs.setShowNotifPref(flag)
    }
    fun setCheckDataPref(flag: Boolean){
        prefs.setCheckDataPref(flag)
    }
    fun setFirstLaunch(flag: Boolean){
        prefs.setFirstLaunchPref(flag)
    }

    fun getShowArchivePref(): Boolean = prefs.getShowArchivePref()
    fun getShowNotifPref(): Boolean = prefs.getShowNotifPref()
    fun getCheckDataPref(): Boolean = prefs.getCheckDatafPref()
    fun getFirstLaunch(): Boolean = prefs.getFirstLaunchfPref()

    fun removeArchive(){
        repo.removeArchiveData()
    }

    fun unmarkAllNotifications() = repo.unmarkAllNotifications()

    fun getMarkedNotifications() = repo.getMarkedNotifications()

    fun clearCachedData() {
        repo.clearData()
    }

    fun checkNotificationId(id: Int) = repo.checkNotificationId(id)

    fun createNotificationRightNow(date: String, notif: String){
        val intent = Intent(App.instance.applicationContext, NotificationReceiver::class.java)
        val title = date
        val message = notif
        intent.putExtra(AppConstants.TITLE_EXTRA, title)
        intent.putExtra(AppConstants.MESSAGE_EXTRA, message)
        val pendingIntent = PendingIntent.getBroadcast(
            App.instance.applicationContext,
            AppConstants.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Получаем доступ к AlarmManager
        val alarmManager =
            App.instance.applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Устанавливаем Alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            1000,
            pendingIntent
        )
    }


}