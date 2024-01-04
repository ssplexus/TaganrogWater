/*
 * Copyright (c) Vasyutchenko Alexey  2023. Last modified 10.12.2023, 19:55
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.data.entity.NotificationsData
import ru.ssnexus.taganrogwater.databinding.ActivitySettingsBinding
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import timber.log.Timber

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = resources.getString(R.string.settings)

        binding.showArchive.isChecked = App.instance.interactor.getShowArchivePref()
        binding.showNotifications.isChecked = App.instance.interactor.getShowNotifPref()
        binding.checkData.isChecked = App.instance.interactor.getCheckDataPref()
        
        binding.showArchive.setOnClickListener {
            App.instance.interactor.setShowArchivePref((it as SwitchCompat).isChecked)
        }
        binding.showNotifications.setOnClickListener {
            App.instance.interactor.setShowNotifPref((it as SwitchCompat).isChecked)
        }
        binding.checkData.setOnClickListener {
            App.instance.interactor.setCheckDataPref((it as SwitchCompat).isChecked)
            if((it as SwitchCompat).isChecked)
                NotificationHelper.cancelCheckDataAlarm(App.instance.applicationContext)
            else
                NotificationHelper.createCheckDataAlarm(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)
        }

        binding.clearArchive.setOnClickListener {

            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.archive_delete))
                .setMessage(getString(R.string.do_you_want_to_delete_archive))
                .setPositiveButton(getString(R.string.yes)){ _, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        App.instance.interactor.removeArchive()
                    }
                }
                .setNegativeButton(getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_water))
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_water))
        }

        binding.clearNotifications.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle(getString(R.string.notifs_delete))
                .setMessage(getString(R.string.do_you_want_to_delete_notifs))
                .setPositiveButton(getString(R.string.yes)){ _, _ ->
                    var markedList = ArrayList<NotificationsData>()
                    CoroutineScope(Dispatchers.IO).launch {
                        markedList = App.instance.interactor.getMarkedNotifications() as ArrayList<NotificationsData>
                        App.instance.interactor.unmarkAllNotifications()
                        markedList.forEach {
                            NotificationHelper.cancelNotificationAlarm(App.instance.applicationContext,
                                it.id
                            )
                        }
                    }
                }
                .setNegativeButton(getString(R.string.no)){ dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_water))
            customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_water))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            android.R.id.home ->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
//        Timber.d("SettingsActivity Destroyed")
        super.onDestroy()
    }

}