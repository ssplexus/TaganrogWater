package ru.ssnexus.taganrogwater

import android.app.Application
import android.content.res.Configuration
import android.os.Build
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import ru.ssnexus.taganrogwater.data.MainRepository
import ru.ssnexus.taganrogwater.data.db.AppDatabase
import ru.ssnexus.taganrogwater.domain.Interactor
import ru.ssnexus.taganrogwater.preferences.PreferencesProvider
import ru.ssnexus.taganrogwater.utils.AutoDisposable
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

        repository = MainRepository(databaseBuilder(
            this,
            AppDatabase::class.java,
            AppConstants.DB_NAME
        ).fallbackToDestructiveMigration().build().notificationDao())

        prefsProvider = PreferencesProvider(this)

        interactor = Interactor(repository, prefsProvider)

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