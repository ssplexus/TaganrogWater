package ru.ssnexus.taganrogwater.activity

import android.app.ProgressDialog
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.NotificationAdapter
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityMainBinding
import ru.ssnexus.taganrogwater.receivers.NotificationReceiver
import ru.ssnexus.taganrogwater.utils.AutoDisposable
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import ru.ssnexus.taganrogwater.utils.NotificationHelper.createCheckDataAlarm
import ru.ssnexus.taganrogwater.utils.Utils
import ru.ssnexus.taganrogwater.viewmodel.MainViewModel
import timber.log.Timber
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(MainViewModel::class.java)
    }

    val autoDisposable = AutoDisposable()
    private lateinit var progressDialog: ProgressDialog

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var notificationAdapter: NotificationAdapter

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }
        autoDisposable.bindTo(this.lifecycle)
        initLayout()
    }

    private fun closeApp(){
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.do_you_want_to_exit))
            .setPositiveButton(getString(R.string.yes)){ _, _ ->
//                exitProcess(1)
                this@MainActivity.finish()
                exitProcess(0)
            }
            .setNegativeButton(getString(R.string.no)){ dialog, _ ->
                dialog.dismiss()
            }
        val customDialog = builder.create()
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_water))
        customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(resources.getColor(R.color.dark_water))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions_menu, menu)
        val linkText = findViewById<TextView>(R.id.infoText)
        linkText.movementMethod = LinkMovementMethod.getInstance()
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.actionId){
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
        }
        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initLayout(){
        setTheme(R.style.Theme_TaganrogWater)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.operInfoRV.setHasFixedSize(true)
        binding.operInfoRV.setItemViewCacheSize(15)
        binding.operInfoRV.layoutManager = LinearLayoutManager(this@MainActivity)
        notificationAdapter = NotificationAdapter(this@MainActivity, ArrayList())
        binding.operInfoRV.adapter = notificationAdapter

        if(Utils.checkConnection(this))
            App.instance.interactor.getData()
        else
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show()

        App.instance.interactor.getNotificationLiveData().observe(this){
            Timber.d("notificationAdapter.updateNotificationsList(it)")
            notificationAdapter.updateNotificationsList(it)
            if(progressDialog.isShowing) progressDialog.dismiss()
        }

        binding.navView.setNavigationItemSelectedListener {
            when(it.itemId)
            {
                R.id.settings -> {
                    onBackPressed()
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                }
                R.id.contacts -> {
                    onBackPressed()
                    startActivity(Intent(this@MainActivity, ContactsActivity::class.java))
                }
                R.id.about -> Toast.makeText(baseContext, "About", Toast.LENGTH_SHORT).show()
                R.id.exit -> {
                    closeApp()
                }
            }
            true
        }
        App.instance.interactor.initDataObservable(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(resources.getString(R.string.loading_please_wait))
        progressDialog.show()

//        NotificationHelper.createNotificationEvent(this, 5000, "01.02.2023", "XoXo")

        if(App.instance.interactor.getCheckDataPref())
            NotificationHelper.enableCheckDataAlarm(App.instance.applicationContext)
    }

    override fun onBackPressed() {
        if(binding.navView.isShown) binding.root.closeDrawers() else  closeApp()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        notificationAdapter.updateNotificationsList(App.instance.interactor.getNotificationCachedList())
    }
}