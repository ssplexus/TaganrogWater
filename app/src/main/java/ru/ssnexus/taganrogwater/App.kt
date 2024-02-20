package ru.ssnexus.taganrogwater

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.res.Configuration
import android.os.Build
import androidx.room.Room.databaseBuilder
import ru.ssnexus.taganrogwater.AppConstants.CHANNEL_DESCR
import ru.ssnexus.taganrogwater.AppConstants.CHANNEL_ID
import ru.ssnexus.taganrogwater.AppConstants.CHANNEL_NAME
import ru.ssnexus.taganrogwater.data.MainRepository
import ru.ssnexus.taganrogwater.data.db.AppDatabase
import ru.ssnexus.taganrogwater.domain.Interactor
import ru.ssnexus.taganrogwater.preferences.PreferencesProvider
import timber.log.Timber

class App: Application() {

    private lateinit var repository: MainRepository
    private lateinit var prefsProvider: PreferencesProvider

    lateinit var interactor: Interactor

    override fun onCreate() {
        super.onCreate()

        if(BuildConfig.DEBUG)
        {
            Timber.plant(Timber.DebugTree())
        }

        instance = this

        createNotificationChannel()

        repository = MainRepository(databaseBuilder(
            this,
            AppDatabase::class.java,
            AppConstants.DB_NAME
        ).fallbackToDestructiveMigration().build().notificationDao())

        prefsProvider = PreferencesProvider(this)

        interactor = Interactor(repository, prefsProvider)

    }


    fun createNotificationChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Задаем имя, описание и важность канала
            //Создаем канал, передав в параметры его ID(строка), имя(строка), важность(константа)
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
//            mChannel.setSound(null, null)
            //Отдельно задаем описание
            mChannel.description = CHANNEL_DESCR
            //Получаем доступ к менеджеру нотификаций
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            //Регистрируем канал
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    // Вызывается при изменении конфигурации, например, поворот
    // Этот метод тоже не обязателен к предопределению
    override fun onConfigurationChanged ( newConfig : Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    // Этот метод вызывается, когда у системы остается мало оперативной памяти
    // и система хочет, чтобы запущенные приложения поумерили аппетиты
    // Переопределять необязательно
    override fun onLowMemory() {
        super.onLowMemory()
    }

    companion object {
        lateinit var instance: App
            private set
    }
}