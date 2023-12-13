package ru.ssnexus.taganrogwater.utils

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.AUDIO_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import com.google.android.gms.common.wrappers.Wrappers.packageManager
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.activity.DetailsActivity
import ru.ssnexus.taganrogwater.activity.MainActivity
import ru.ssnexus.taganrogwater.receivers.NotificationReceiver
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*


object NotificationHelper {

    fun createNotification(context: Context, id: Int, date: String, notif: String){
        Timber.d("createNotification!!!")
        val title = date
        val message = notif//.take(256)

        val mIntent = Intent(context, DetailsActivity::class.java)
        mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        mIntent.putExtra(AppConstants.ID_EXTRA, id)
        mIntent.putExtra(AppConstants.TITLE_EXTRA, title)
        mIntent.putExtra(AppConstants.MESSAGE_EXTRA, message)

        val pendingIntent =
            PendingIntent.getActivity(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(context, AppConstants.CHANNEL_ID)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources,
                R.drawable.splash_screen))
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

    fun createNotificationEvent(context: Context, dateTimeInMillis: Long, date: String, notif: String){
        val intent = Intent(context, NotificationReceiver::class.java)
        val title = date
        val message = notif
        intent.putExtra(AppConstants.TITLE_EXTRA, title)
        intent.putExtra(AppConstants.MESSAGE_EXTRA, message)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AppConstants.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //Устанавливаем Alarm
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            dateTimeInMillis,
            pendingIntent
        )
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun isPresentCheckDataAlarm(context: Context): Boolean{
        val intent = Intent(context, NotificationReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE) != null
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun createCheckDataAlarm(context: Context, period: Long){
        Timber.d("createCheckDataAlarm!!!")
        //Получаем доступ к AlarmManager
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val alarmIntent:PendingIntent
                = Intent(context, NotificationReceiver::class.java).let { intent ->
            intent.action = AppConstants.ACTION_CHECKDATA
            PendingIntent.getBroadcast(context,
                0,
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

    fun cancelCheckDataAlarm(context: Context){
        Timber.d("cancelCheckDataAlarm!!!")
        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationReceiver::class.java)
        intent.action = AppConstants.ACTION_CHECKDATA
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.cancel(pendingIntent)
    }

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

    fun enableCheckDataAlarm(context: Context){
        val receiver = ComponentName(context, NotificationReceiver::class.java)

        if(context.packageManager.getComponentEnabledSetting(receiver) !=
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED){
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
            if(isPresentCheckDataAlarm(context)) cancelCheckDataAlarm(context)
            createCheckDataAlarm(context, AppConstants.CHECKDATA_PERIOD)
        }else if(!isPresentCheckDataAlarm(context))
            createCheckDataAlarm(context, AppConstants.CHECKDATA_PERIOD)
    }

    fun disableCheckDataAlarm(context: Context){
        cancelCheckDataAlarm(context)
        val receiver = ComponentName(context, NotificationReceiver::class.java)
        context.packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}