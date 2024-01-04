package ru.ssnexus.taganrogwater.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import timber.log.Timber
import java.text.ParseException


class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            if(App.instance.interactor.getCheckDataPref())
            {
                //NotificationHelper.createNotification(App.instance.applicationContext,
                  //  Random().nextInt(1000),"01.01.2024", "Alarm Created")
                NotificationHelper.createCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)

                CoroutineScope(Dispatchers.IO).launch {

                        App.instance.interactor.getNotificationsListFromDB().forEach {
                        if(it.marked > 0) {
                            var dateFromNotif:String = ""
                            val formatter = SimpleDateFormat("dd.MM.yyyy")
                            try{
                                dateFromNotif = formatter.format(it.date)
                            } catch (e: ParseException){
                                e.printStackTrace()
                            }
                            val period = it.marked * 60 * 60 * 1000L
                            NotificationHelper.createNotificationAlarm(App.instance.applicationContext,
                                it.id, dateFromNotif, it.notifiction, period)
                        }
                    }
                }
            }
        }
        if (intent.action == AppConstants.ACTION_CHECKDATA){
            Timber.d("Check data!!!")
            //NotificationHelper.createNotification(App.instance.applicationContext,
              //  1000 + Random().nextInt(1000),"Check data", "Get data!!!")
                App.instance.interactor.getData()
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