package ru.ssnexus.taganrogwater.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.activity.DetailsActivity
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import timber.log.Timber
import java.text.ParseException
import java.util.*
import kotlin.random.Random.Default.nextInt


class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            if(App.instance.interactor.getCheckDataPref())
            {
//                NotificationHelper.enableCheckDataAlarm(App.instance.applicationContext)
//                NotificationHelper.createNotification(App.instance.applicationContext,
//                    Random().nextInt(1000),"01.01.2024", "Alarm Created")
                NotificationHelper.createCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)

                if(App.instance.interactor.getNotificationCachedList().isEmpty()){
                    NotificationHelper.createNotification(App.instance.applicationContext,
                        Random().nextInt(1000),"01.01.2024", "Alarm Created")
                }
//                App.instance.interactor.getNotificationCachedList().forEach {
//                    if(it.marked > 0) {
//                        var dateFromNotif:String = ""
//                        val formatter = SimpleDateFormat("dd.MM.yyyy")
//                        try{
//                            dateFromNotif = formatter.format(it.date)
//                        } catch (e: ParseException){
//                            e.printStackTrace()
//                        }
//                        val period = 10000L
//                        //it.marked * 60 * 60 * 1000
//                        NotificationHelper.createNotificationAlarm(App.instance.applicationContext,
//                            it.id, dateFromNotif, it.notifiction, period)
//                    }
//                }
            }
        }
        if (intent.action == AppConstants.ACTION_CHECKDATA){
//            Timber.d("Receive check data!!!")
                App.instance.interactor.getData()
//            NotificationHelper.createNotification(App.instance.applicationContext,
//                Random().nextInt(1000),"01.01.2001", "XXX")
        }
        if(intent.action?.contains(AppConstants.ACTION_NOTIF_PREFIX) == true){
            val extras = intent.extras
            if(extras != null)
            {
                Timber.d("ACTION_NOTIF_PREFIX Extras!!!")
                NotificationHelper.createNotification(App.instance.applicationContext,
                    extras.getInt(AppConstants.ID_EXTRA),
                    extras.getString(AppConstants.TITLE_EXTRA,""),
                    extras.getString(AppConstants.MESSAGE_EXTRA, ""))
            }
        }
    }
}