package ru.ssnexus.taganrogwater.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import timber.log.Timber
import java.util.*
import kotlin.random.Random.Default.nextInt


class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            if(App.instance.interactor.getCheckDataPref())
            {
//                NotificationHelper.enableCheckDataAlarm(App.instance.applicationContext)
                NotificationHelper.createCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)
            }
        }
        if (intent.action == AppConstants.ACTION_CHECKDATA){
//            Timber.d("Receive check data!!!")
                App.instance.interactor.getData()
//            NotificationHelper.createNotification(App.instance.applicationContext,
//                Random().nextInt(1000),"01.01.2001", "XXX")
        }
    }
}