/*
 * Copyright (c) Vasyutchenko Alexey  2023. Last modified 17.12.2023, 22:45
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityDetailsBinding
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import ru.ssnexus.taganrogwater.utils.Utils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var menu: Menu? = null

    companion object {
        var notificationId: Int = 0
        var notificationMarked: Int = -1
        var notificationDate: Long = 0
        var notificationBody: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var strDate: String = ""
        val formatter = SimpleDateFormat("dd.MM.yyyy")
        if(savedInstanceState == null)
        {
//            Timber.d("savedInstanceState")
            val extras = intent.extras
            if(extras != null)
            {
//                Timber.d("Extras!!!")
                notificationId = extras.getInt(AppConstants.ID_EXTRA)
                strDate = extras.getString(AppConstants.TITLE_EXTRA,"")

                try {
                    val date = formatter.parse(strDate) as Date
                    notificationDate = date.time
                } catch (e:ParseException){
                    e.printStackTrace()
                }
                runBlocking {
                    val job: Job = launch(context = Dispatchers.Default) {
                        notificationMarked = App.instance.interactor.getMarkedStateById(notificationId)
                    }
                    job.join()
                }

                notificationBody = extras.getString(AppConstants.MESSAGE_EXTRA, "")
            }
        }

        if(strDate.isEmpty()){
            try{
                strDate = formatter.format(notificationDate)
            } catch (e: ParseException){
                e.printStackTrace()
            }
        }

        binding.notifDate.text = strDate
        binding.notifBody.text = notificationBody
        binding.notifBody.movementMethod = ScrollingMovementMethod()

        if(notificationMarked == 0){
            binding.notifDate.setBackgroundColor(resources.getColor(R.color.gray))
            supportActionBar?.title = resources.getString(R.string.archive_info)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.gray)))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        if (menu != null) {
            this.menu = menu
            this.menu?.let {
                when (notificationMarked){
                    -1 -> it.getItem(0).icon = resources.getDrawable(R.drawable.star_outline_icon, theme)
                     0 -> it.getItem(0).icon = resources.getDrawable(R.drawable.delete_icon, theme)
                     in (1..24) -> it.getItem(0).icon = resources.getDrawable(R.drawable.star_rate_icon, theme)
                }
                it.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
            }

        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            R.id.actionId ->{
                var notifDeletelFlag = false
                val builder = MaterialAlertDialogBuilder(this@DetailsActivity)
                builder.setTitle(getString(R.string.period_settings))
                    .setMessage(getString(

                        when(notificationMarked){
                            -1 -> R.string.do_you_want_to_set_mark
                            in (1..24) -> R.string.do_you_want_to_remove_mark
                            else -> R.string.do_you_want_to_remove_notification
                        }))
                    .setPositiveButton(getString(R.string.yes)){ dialog, _ ->

                        when(notificationMarked) {
                            0 -> {
                                CoroutineScope(Dispatchers.IO).launch {
                                        App.instance.interactor.removeNotificationById(
                                            notificationId)
                                        notifDeletelFlag = true
                                }
                            }
                            else->{
                                this@DetailsActivity.menu?.let { menu ->
                                    when (notificationMarked) {
                                         -1 -> {
                                                    Utils.showSettingsDialog(this@DetailsActivity, DialogInterface.OnDismissListener {
                                                        if(notificationMarked > 0){
                                                            menu.getItem(0).icon = resources.getDrawable(R.drawable.star_rate_icon, theme)
                                                            menu.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
                                                            CoroutineScope(Dispatchers.IO).launch {
                                                                App.instance.interactor.setMarkedStateById(
                                                                    notificationId, notificationMarked)
                                                            }

                                                            val period = notificationMarked * 60 * 60 * 1000L
                                                            NotificationHelper.createNotificationAlarm(App.instance.applicationContext,
                                                                notificationId,
                                                                binding.notifDate.text as String,
                                                                notificationBody,
                                                                period)
                                                        }
                                                     })
                                                    dialog.dismiss()
                                         }
                                        else -> {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                App.instance.interactor.setMarkedStateById(
                                                    notificationId, -1)
                                            }
                                            NotificationHelper.cancelNotificationAlarm(App.instance.applicationContext, notificationId)
                                            menu.getItem(0).icon = resources.getDrawable(R.drawable.star_outline_icon, theme)
                                            menu.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    .setNegativeButton(getString(R.string.no)){ dialog, _ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()

                customDialog.setOnDismissListener {
                    if(notifDeletelFlag) onBackPressed()
                }
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_water))
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_water))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
//        Timber.d("DetailsActivity Destroyed")
        super.onDestroy()
    }
}