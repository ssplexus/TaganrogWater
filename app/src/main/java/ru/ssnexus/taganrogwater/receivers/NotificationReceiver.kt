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

        // Пересоздание напоминаний и будильников в случае перезагрузки устройства
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            //NotificationHelper.createNotification(App.instance.applicationContext,
              //  Random().nextInt(1000),"01.01.2024", "Alarm Created")

            // Создание будильника опроса сайта
            NotificationHelper.createCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)

            // Пересоздание напоминаний
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

        // Если событие запроса данных с сайта
        if (intent.action == AppConstants.ACTION_CHECKDATA){
            Timber.d("ACTION_CHECKDATA")
            // Опрос сайта
            if(App.instance.interactor.getCheckDataPref()) App.instance.interactor.getData()
            NotificationHelper.createCheckDataAlarmOneShot(
                App.instance.applicationContext,
                AppConstants.CHECKDATA_PERIOD
            )
        }

        // Если событие нажатие на оповещение
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