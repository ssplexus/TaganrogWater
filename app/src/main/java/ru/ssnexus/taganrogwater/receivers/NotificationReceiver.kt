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
import ru.ssnexus.taganrogwater.utils.NotificationHelper.createCheckCheckDataAlarm
import timber.log.Timber
import java.text.ParseException


class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        // Пересоздание напоминаний и будильников в случае перезагрузки устройства
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            //NotificationHelper.createNotification(App.instance.applicationContext,
              //  Random().nextInt(1000),"01.01.2024", "Alarm Created")

            // Создание будильника проверки будильника опроса сайта на случай прерывания его (страхующий будильник)
            NotificationHelper.createCheckCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECK_CHECKDATA_ALARM_PERIOD)
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
            //NotificationHelper.createNotification(App.instance.applicationContext,
              //  1000 + Random().nextInt(1000),"Check data", "Get data!!!")

            // Если страхующий будильник прервался то пересоздаём
            if(!NotificationHelper.isPresentAlarm(App.instance.applicationContext,
                    AppConstants.ACTION_CHECK_CHECKDATA_ALARM,
                    AppConstants.CHECK_CHECKDATA_ALARM_REQUEST_CODE)){
                App.instance.interactor.appendLog("RECREATE CHECK_CHECKDATA_ALARM")
                createCheckCheckDataAlarm(App.instance.applicationContext,
                    AppConstants.CHECK_CHECKDATA_ALARM_PERIOD)
            }

            // Опрос сайта
            if(App.instance.interactor.getCheckDataPref()) App.instance.interactor.getData()
        }

        // Если событие проверки будильника опроса сайта
        if (intent.action == AppConstants.ACTION_CHECK_CHECKDATA_ALARM){
            App.instance.interactor.appendLog("ACTION_CHECK_CHECKDATA_ALARM")

            // Если установлен параметр проверки данных
            if(App.instance.interactor.getCheckDataPref()){
                // Если будильник опроса не создан, то создаём
                if(!NotificationHelper.isPresentAlarm(App.instance.applicationContext,
                        AppConstants.ACTION_CHECKDATA,
                        AppConstants.CHECKDATA_ALARM_REQUEST_CODE)){
                    App.instance.interactor.appendLog("RECREATE CHECKDATA_ALARM")
                    NotificationHelper.createCheckDataAlarm(
                        App.instance.applicationContext,
                        AppConstants.CHECKDATA_PERIOD
                    )
                }
            }
            // Создание страхующего будильника для будильника опроса сайта
            NotificationHelper.createCheckCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECK_CHECKDATA_ALARM_PERIOD)
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