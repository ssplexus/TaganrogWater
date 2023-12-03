package ru.ssnexus.taganrogwater.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.activity.DetailsActivity.Companion.notificationId
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import ru.ssnexus.taganrogwater.utils.Utils
import timber.log.Timber


class NotificationReceiver: BroadcastReceiver() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
//            if(App.instance.interactor.getCheckDataPref())
//                NotificationHelper.enableCheckDataAlarm(App.instance.applicationContext)
        }
        if (intent.action == AppConstants.ACTION_CHECKDATA){
            Timber.d("Receive check data!!!")
//            if(Utils.checkConnection(context))
                App.instance.interactor.getData()
//            else
//                Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_SHORT).show()
        }


    }
}