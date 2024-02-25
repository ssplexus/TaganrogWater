/*
 * Copyright (c) Vasyutchenko Alexey  2023. Last modified 31.12.2023, 12:42
 * ss.plexus@gmail.com
 */

package ru.ssnexus.taganrogwater.activity

import android.Manifest
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.BuildConfig
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.AppConstants
import ru.ssnexus.taganrogwater.AppConstants.STORAGE_PERMISSION_REQUEST_CODE
import ru.ssnexus.taganrogwater.NotificationAdapter
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.databinding.ActivityMainBinding
import ru.ssnexus.taganrogwater.utils.AutoDisposable
import ru.ssnexus.taganrogwater.utils.NotificationHelper
import ru.ssnexus.taganrogwater.utils.NotificationHelper.createCheckDataAlarmOneShot
import ru.ssnexus.taganrogwater.utils.Utils
import ru.ssnexus.taganrogwater.viewmodel.MainViewModel
import timber.log.Timber
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider.NewInstanceFactory().create(MainViewModel::class.java)
    }
    companion object{
        lateinit var mSearchView: SearchView
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

        //Пытаемся получить токен с помощью слушателя
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                //Если не удастся получить токен, то логируем причину и выходим из слушателя
                if (!task.isSuccessful) {
                    Timber.w("MainActivity Fetching FCM registration token failed" + task.exception);
                    return@OnCompleteListener
                }
                //Если получилось, то логируем токен
                Timber.i("MainActivity" + task.result!!)
            })

        AboutActivity.author_text = resources.getText(R.string.author_str).toString()
        // Получение параметра из Remote Config
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val mFirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder().build()
        mFirebaseRemoteConfig.setConfigSettingsAsync(mFirebaseRemoteConfigSettings)
        mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(this) {task ->
            if(task.isSuccessful)
                mFirebaseRemoteConfig.activate()
                val fbVal = mFirebaseRemoteConfig.getString("email_val")
                AboutActivity.author_text += "\n" + fbVal
        }

        // Проверяем, есть ли разрешение WRITE_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Уже есть разрешение, выполняем операции записи в хранилище
            App.instance.interactor.appendLog("App Started")
        } else {
            // Разрешение не предоставлено, запрашиваем его у пользователя
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }

        // Метод инициализации
        initLayout()
    }

    private fun closeApp(){
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(getString(R.string.exit))
            .setMessage(getString(R.string.do_you_want_to_exit))
            .setPositiveButton(getString(R.string.yes)){ _, _ ->
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
        // Создание меню
        menuInflater.inflate(R.menu.actions_menu, menu)
        // Добавление активных ссылок в TextView
        val linkText = findViewById<TextView>(R.id.infoText)
        linkText.movementMethod = LinkMovementMethod.getInstance()
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        // Добавление действия для кнопки в правом верхнем углу формы
        if (id == R.id.actionId){
            binding.searchView.clearFocus()
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
        mSearchView = binding.searchView
        setContentView(binding.root)

        // Добавляем главное меню
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        // Отображаем главное меню
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(resources.getString(R.string.loading_please_wait))
        progressDialog.setCancelable(false)

        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText != null) notificationAdapter.filterSearchList(newText)
                else notificationAdapter.filterSearchList("")
                return true
            }
        })

        // Инициализация RView
        binding.operInfoRV.setHasFixedSize(true)
        binding.operInfoRV.setItemViewCacheSize(15)
        binding.operInfoRV.layoutManager = LinearLayoutManager(this@MainActivity)
        notificationAdapter = NotificationAdapter(this@MainActivity, ArrayList())
        binding.operInfoRV.adapter = notificationAdapter

        // Наблюдение за данными в базе сообщений
        App.instance.interactor.getNotificationLiveData().observe(this){
//            Timber.d("notificationAdapter.f(it)%s", it.toString())
            // Обновление RV
            if(!it.isEmpty()) {
                binding.noDataView.visibility = View.GONE
                binding.pullToRefresh.visibility = View.VISIBLE
                binding.operInfoRV.visibility = View.VISIBLE
            }
            else{
                binding.operInfoRV.visibility = View.GONE
                binding.noDataView.visibility = View.VISIBLE
            }

            notificationAdapter.updateNotificationsList(it)
        }

        //Наблюдение за результатом опроса сайта и оповещение пользователя
        App.instance.interactor.getCheckDataResultLiveData().observe(this){
//            Timber.d("getCheckDataResultLiveData%s", it)
            if(progressDialog.isShowing) progressDialog.dismiss()
            if(binding.pullToRefresh.isRefreshing) binding.pullToRefresh.isRefreshing = false
            var hideRVFlag = false
            if(!Utils.checkConnection(this)) {
                Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                hideRVFlag = true
            }
            else
                if(!it) {
                    Toast.makeText(this, getString(R.string.get_data_failed), Toast.LENGTH_LONG).show()
                    hideRVFlag = true
                }
            if(hideRVFlag){
                val adapter = binding.operInfoRV.adapter
                if(adapter?.itemCount == 0){
                    binding.pullToRefresh.visibility = View.GONE
                    binding.noDataView.visibility = View.VISIBLE
                }
            }
        }

        binding.refreshBtn.setOnClickListener {
            binding.noDataView.visibility = View.GONE
            progressDialog.show()
            App.instance.interactor.getData()
        }

        //Вешаем слушатель, чтобы вызвался pull to refresh
        binding.pullToRefresh.setOnRefreshListener {
            progressDialog.show()
            App.instance.interactor.getData()
        }

        // Добавление действий для главного меню
        initNavMenu()

        //Инициализация наблюдения за изиенением данных в БД
        App.instance.interactor.initDataObservable(this)

        // Если программа запускается в первый раз
        if(App.instance.interactor.getFirstLaunch())
        {
            // Активизируем приёмник
            NotificationHelper.setEnableReceiver(App.instance.applicationContext, true )
            App.instance.interactor.setFirstLaunch(false)

            progressDialog.show()
            val builder = MaterialAlertDialogBuilder(this, R.style.MaterialAlertDialog_Center)
            builder.setTitle(getString(R.string.greetings))
                .setMessage(getString(R.string.first_launch_app))
                .setPositiveButton(getString(R.string.ok)){ dialog, _ ->
                    // Проверка сети и оповещение пользователя
                    if(!Utils.checkConnection(this)) {
                        Toast.makeText(this, getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show()
                        val adapter = binding.operInfoRV.adapter
                        if(adapter?.itemCount == 0){
                            binding.operInfoRV.visibility = View.GONE
                            binding.noDataView.visibility = View.VISIBLE
                        }
                    }
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()
            customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(resources.getColor(R.color.dark_water))
        }

        Timber.d("Main Started!!!")
        // Если будильник опроса не создан, то создаём

        if(!NotificationHelper.isPresentAlarm(App.instance.applicationContext,
                AppConstants.ACTION_CHECKDATA,
                AppConstants.CHECKDATA_ALARM_REQUEST_CODE)){
            App.instance.interactor.appendLog("FirstLaunch")
            App.instance.interactor.getData()
            createCheckDataAlarmOneShot(App.instance.applicationContext, AppConstants.CHECKDATA_PERIOD)
        }
    }

    private fun initNavMenu(){
        // Добавление действий для главного меню
        binding.navView.setNavigationItemSelectedListener {
            binding.searchView.clearFocus()
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
                R.id.help -> {
                    onBackPressed()
                    startActivity(Intent(this@MainActivity, HelpActivity::class.java))
                }
                R.id.about -> {
                    onBackPressed()
                    startActivity(Intent(this@MainActivity, AboutActivity::class.java))
                }
                R.id.exit -> {
                    closeApp()
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        if(binding.navView.isShown) binding.root.closeDrawers() else  closeApp()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        Timber.d("onResume MainActivity")
        notificationAdapter.searchQuery = binding.searchView.query.toString()
        notificationAdapter.updateNotificationsList(App.instance.interactor.getNotificationsCachedList())
    }

    // Метод вызывается после ответа пользователя на запрос разрешения
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Разрешение предоставлено, выполняем операции записи в хранилище
                    App.instance.interactor.appendLog("App Started")
                } else {
                    // Разрешение не предоставлено, выводим сообщение об ошибке
                    Toast.makeText(
                        this,
                        "Разрешение на запись во внешнее хранилище не предоставлено",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}