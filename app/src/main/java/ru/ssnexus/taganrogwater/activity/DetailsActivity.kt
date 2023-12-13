package ru.ssnexus.taganrogwater.activity

import android.content.DialogInterface
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityDetailsBinding
import ru.ssnexus.taganrogwater.utils.Utils
import timber.log.Timber

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding
    private var menu: Menu? = null

    companion object {
        var notificationId: Int = 0
        var notificationMarked: Int = -1
        var notificationDate: String = ""
        var notificationBody: String = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(savedInstanceState == null)
        {
            Timber.d("savedInstanceState")
            val extras = intent.extras
            if(extras != null)
            {
                Timber.d("Extras!!!")
                notificationId = extras.getInt(AppConstants.ID_EXTRA)
                notificationDate = extras.getString(AppConstants.TITLE_EXTRA,"")
                notificationBody = extras.getString(AppConstants.MESSAGE_EXTRA, "")
            }
        }

        binding.notifDate.text = notificationDate
        binding.notifBody.text = notificationBody
        binding.notifBody.movementMethod = ScrollingMovementMethod()

        if(notificationMarked == 0){
            binding.notifDate.setBackgroundColor(resources.getColor(R.color.gray))
            supportActionBar?.title = resources.getString(R.string.archive_info)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.gray)))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        runBlocking {
//            val job: Job = launch(context = Dispatchers.Default) {
//                Timber.d("getMarkedStateById = " + App.instance.interactor.getMarkedStateById(10000))
//            }
//        }

//        App.instance.interactor.getNotificationLiveData().observe(this@DetailsActivity){
//            var marked: Int = -2
//            runBlocking {
//                val job: Job = launch(context = Dispatchers.Default) {
//                    marked = App.instance.interactor.getMarkedStateById(notificationId)
//                }
//                job.join()
//
//                if(marked != notificationMarked && marked != -2){
//                    this@DetailsActivity.menu?.let {
//                        notificationMarked = marked
//                        when (notificationMarked){
//                            -1 -> it.getItem(0).icon = resources.getDrawable(R.drawable.star_outline_icon, theme)
//                             0 -> it.getItem(0).icon = resources.getDrawable(R.drawable.delete_icon, theme)
//                             1 -> it.getItem(0).icon = resources.getDrawable(R.drawable.star_rate_icon, theme)
//                        }
//                        it.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
//                    }
//                }
//            }
//        }(1..24)
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
                                                        }
                                                     })
                                                    dialog.dismiss()
                                         }
                                        else -> {
                                            CoroutineScope(Dispatchers.IO).launch {
                                                App.instance.interactor.removeNotificationById(
                                                    notificationId)
                                            }
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
        Timber.d("DetailsActivity Destroyed")
        super.onDestroy()
    }
}