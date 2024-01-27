package ru.ssnexus.taganrogwater.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.activity.DetailsActivity
import ru.ssnexus.taganrogwater.receivers.NotificationReceiver
import timber.log.Timber


object NotificationHelper {

    // Создание оповещения
    fun createNotification(context: Context, id: Int, date: String, notif: String){
//        Timber.d("createNotification!!!")
        if(App.instance.interactor.getShowNotifPref()){
            val title = date
            val message = notif//.take(256)

            val mIntent = Intent(context, DetailsActivity::class.java)
            mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            mIntent.putExtra(AppConstants.ID_EXTRA, id)
            mIntent.putExtra(AppConstants.TITLE_EXTRA, title)
            mIntent.putExtra(AppConstants.MESSAGE_EXTRA, message)

            val pendingIntent =
                PendingIntent.getActivity(context, id, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)

            val notification = NotificationCompat.Builder(context, AppConstants.CHANNEL_ID)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                    if(Build.VERSION.SDK_INT <=  Build.VERSION_CODES.M ) R.drawable.tgnwater_icon_splashscreen else R.drawable.splash_screen))
                .setSmallIcon(R.drawable.water_drop_icon)
                .setContentTitle(title)
                .setContentText(message)
                .setColor(context.resources.getColor(R.color.dark_water))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(message))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(id, notification)
        }
    }

    // Проверка наличия будильника
    @SuppressLint("UnspecifiedImmutableFlag")
    fun isPresentAlarm(context: Context, requestCode: Int): Boolean{
        val intent = Intent(context, NotificationReceiver::class.java)
        return PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE) != null
    }

    // Создание будильника опроса сайта
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createCheckDataAlarm(context: Context, period: Long){
        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent:PendingIntent
                = Intent(context, NotificationReceiver::class.java).let { intent ->
            intent.action = AppConstants.ACTION_CHECKDATA
            PendingIntent.getBroadcast(context,
                AppConstants.CHECKDATA_ALARM_REQUEST_CODE,
                intent,
                0)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 5000,
            period,
            alarmIntent
        )
    }

    // Создание будильника проверки наличия будильника опроса сайта
    @RequiresApi(Build.VERSION_CODES.M)
    fun createCheckCheckDataAlarm(context: Context, period: Long){
        Timber.d("createCheckCheckDataAlarm!!!")
        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent:PendingIntent
                = Intent(context, NotificationReceiver::class.java).let { intent ->
            intent.action = AppConstants.ACTION_CHECK_CHECKDATA_ALARM
            PendingIntent.getBroadcast(context,
                AppConstants.CHECK_CHECKDATA_ALARM_REQUEST_CODE,
                intent,
                0)
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + period,
            alarmIntent
        )
    }

    // Создание будильника напоминания о событии
    @SuppressLint("UnspecifiedImmutableFlag")
    fun createNotificationAlarm(context: Context, id: Int, date: String, notification: String, period: Long){
        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager


        val alarmIntent:PendingIntent
                = Intent(context, NotificationReceiver::class.java).let { intent ->
            intent.action = AppConstants.ACTION_NOTIF_PREFIX + id.toString()

            intent.putExtra(AppConstants.ID_EXTRA, id)
            intent.putExtra(AppConstants.TITLE_EXTRA, date)
            intent.putExtra(AppConstants.MESSAGE_EXTRA, notification)

            PendingIntent.getBroadcast(context,
                id,
                intent,
                0)
        }
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + period,
            period,
            alarmIntent
        )
    }

    // Отмена будильника напоминания о событии
    fun cancelNotificationAlarm(context: Context, id: Int){
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.action = AppConstants.ACTION_NOTIF_PREFIX + id.toString()
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    // Отмена будильника проверки данных
    fun cancelCheckDataAlarm(context: Context){
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.action = AppConstants.ACTION_CHECKDATA
        val pendingIntent = PendingIntent.getBroadcast(context, AppConstants.CHECKDATA_ALARM_REQUEST_CODE, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    fun cancelCheckCheckDataAlarm(context: Context){
//        Timber.d("cancelCheckDataAlarm!!!")
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.action = AppConstants.ACTION_CHECK_CHECKDATA_ALARM
        val pendingIntent = PendingIntent.getBroadcast(context, AppConstants.CHECK_CHECKDATA_ALARM_REQUEST_CODE, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

    // Включение/выключение приёмника
    fun setEnableReceiver(context: Context, flag: Boolean){
        val receiver = ComponentName(context, NotificationReceiver::class.java)
        if(flag){
            if(context.packageManager.getComponentEnabledSetting(receiver) !=
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
                context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
        else{
            if(context.packageManager.getComponentEnabledSetting(receiver) !=
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED){
                context.packageManager.setComponentEnabledSetting(
                    receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP
                )
            }
        }
    }
}