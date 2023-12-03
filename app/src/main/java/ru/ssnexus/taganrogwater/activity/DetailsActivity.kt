package ru.ssnexus.taganrogwater.activity

import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityDetailsBinding
import ru.ssnexus.taganrogwater.utils.Utils
import timber.log.Timber

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    private lateinit var menu: Menu

    companion object {
        var notificationId: Int = 0
        var notificationMarked: Int = 0
        lateinit var notificationDate: String
        lateinit var notificationBody: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.notifDate.text = notificationDate
        binding.notifBody.text = notificationBody
        binding.notifBody.movementMethod = ScrollingMovementMethod()

        if(notificationMarked == 0){
            binding.notifDate.setBackgroundColor(resources.getColor(R.color.gray))
            supportActionBar?.title = resources.getString(R.string.archive_info)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.gray)))
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        App.instance.interactor.getNotificationLiveData().observe(this){
            var marked: Int = -2
            runBlocking {
                val job: Job = launch(context = Dispatchers.Default) {
                    marked = App.instance.interactor.getMarkedStateById(notificationId)
                }
                job.join()

                if(marked != notificationMarked && marked != -2){
                    notificationMarked = marked
                    when (notificationMarked){
                        -1 -> this@DetailsActivity.menu.getItem(0).icon = resources.getDrawable(R.drawable.star_outline_icon, theme)
                         0 -> this@DetailsActivity.menu.getItem(0).icon = resources.getDrawable(R.drawable.delete_icon, theme)
                         1 -> this@DetailsActivity.menu.getItem(0).icon = resources.getDrawable(R.drawable.star_rate_icon, theme)
                    }
                    this@DetailsActivity.menu.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
                }
            }
        }

        if(savedInstanceState == null)
        {
            val extras = intent.extras
            if(extras != null)
            {

            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        if (menu != null) {
            this.menu = menu

            when (notificationMarked){
                -1 -> this.menu.getItem(0).icon = resources.getDrawable(R.drawable.star_outline_icon, theme)
                 0 -> this.menu.getItem(0).icon = resources.getDrawable(R.drawable.delete_icon, theme)
                 1 -> this.menu.getItem(0).icon = resources.getDrawable(R.drawable.star_rate_icon, theme)
            }
            this.menu.getItem(0).icon.setColorFilter(resources.getColor(R.color.white) , PorterDuff.Mode.SRC_ATOP)
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
                            1 -> R.string.do_you_want_to_remove_mark
                            else -> R.string.do_you_want_to_remove_notification
                        }))
                    .setPositiveButton(getString(R.string.yes)){ dialog, _ ->
                        runBlocking {
                            val job: Job = launch(context = Dispatchers.Default) {
                                when(notificationMarked){
                                    0 -> {
                                        App.instance.interactor.removeNotificationById(
                                        notificationId)
                                        notifDeletelFlag = true
                                    }
                                    else -> App.instance.interactor.updateMarkedStateById(
                                        notificationId)
                                }
                            }
                            job.join()
                        }

                        if(notificationMarked < 0 ) {
                            Utils.showSettingsDialog(this@DetailsActivity)
                            dialog.dismiss()
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