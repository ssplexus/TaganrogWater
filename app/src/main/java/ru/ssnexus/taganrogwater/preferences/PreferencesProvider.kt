package ru.ssnexus.taganrogwater.preferences

import android.content.Context
import android.content.SharedPreferences
import ru.ssnexus.taganrogwater.App

class PreferencesProvider (context: Context) {
    //Нам нужен контекст приложения
    private val appContext = context.applicationContext
    //Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    init {

    }

    //Ключи для наших настроек, по ним мы их будем получать
    companion object {
        const val KEY_FIRST_LAUNCH = "first_launch"
    }
}