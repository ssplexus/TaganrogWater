package ru.ssnexus.taganrogwater.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import ru.ssnexus.taganrogwater.App
import timber.log.Timber

class PreferencesProvider (context: Context) {
    //Нам нужен контекст приложения
    private val appContext = context.applicationContext
    //Создаем экземпляр SharedPreferences
    private val preference: SharedPreferences = appContext.getSharedPreferences("settings", Context.MODE_PRIVATE)

    init {
        if(preference.getBoolean(KEY_FIRST_LAUNCH, true)) {
            preference.edit { putBoolean(KEY_SHOW_ARCHIVE_SWITCH, true) }
            preference.edit { putBoolean(KEY_SHOW_NOTIF_SWITCH, true) }
            preference.edit { putBoolean(KEY_CHECK_DATA_SWITCH, true) }
            preference.edit { putBoolean(KEY_FIRST_LAUNCH, false) }
            preference.edit { putLong(KEY_FIRST_LAUNCH_TIME, System.currentTimeMillis())}
        }
    }

    fun setShowArchivePref(flag: Boolean){
        preference.edit{putBoolean(KEY_SHOW_ARCHIVE_SWITCH, flag)}
        Timber.d("KEY_SHOW_ARCHIVE_SWITCH=%s", getShowArchivePref())
    }
    fun setShowNotifPref(flag: Boolean){
        preference.edit{putBoolean(KEY_SHOW_NOTIF_SWITCH, flag)}
        Timber.d("KEY_SHOW_NOTIF_SWITCH=%s", getShowNotifPref())
    }
    fun setCheckDataPref(flag: Boolean){
        preference.edit{putBoolean(KEY_CHECK_DATA_SWITCH, flag)}
        Timber.d("KEY_CHECK_DATA_SWITCH=%s", getCheckDatafPref())
    }

    fun getShowArchivePref() = preference.getBoolean(KEY_SHOW_ARCHIVE_SWITCH, true)
    fun getShowNotifPref() = preference.getBoolean(KEY_SHOW_NOTIF_SWITCH, true)
    fun getCheckDatafPref() = preference.getBoolean(KEY_CHECK_DATA_SWITCH, true)

    //Получить время первого запуска
    fun getFirstLaunchTime():Long{
        return preference.getLong(KEY_FIRST_LAUNCH_TIME,0)
    }

    //Ключи для наших настроек, по ним мы их будем получать
    companion object {
        const val KEY_FIRST_LAUNCH = "first_launch"
        const val KEY_FIRST_LAUNCH_TIME = "first_launch_time"
        const val KEY_SHOW_ARCHIVE_SWITCH = "archive_switch"
        const val KEY_SHOW_NOTIF_SWITCH = "show_notif_switch"
        const val KEY_CHECK_DATA_SWITCH = "check_data_switch"
    }
}