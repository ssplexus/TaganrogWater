package ru.ssnexus.taganrogwater

object AppConstants {
    const val DB_VERSION = 3
    const val DB_NAME = "notifications_db"
    const val DATA_URL = "http://www.tgnvoda.ru/avarii.php"
//    const val DATA_URL = "http://192.168.0.14/tagan-water/avarii/avarii.php"

    const val CONTACTS_URL = "http://www.tgnvoda.ru/kontakt.html"

    const val NOTIFICATION_ID = 1
    const val CHANNEL_ID = "channel1"
    const val CHANNEL_NAME = "notif channel"
    const val CHANNEL_DESCR = "A Description of the Channel"
    const val ID_EXTRA = "idExtra"
    const val TITLE_EXTRA = "titleExtra"
    const val MESSAGE_EXTRA = "messaeExtra"

    const val ACTION_CHECKDATA = "CheckData"
    const val ACTION_NOTIF_PREFIX = "Notif_"
    const val ACTION_CHECK_CHECKDATA_ALARM = "CheckDataAlarm"


    const val CHECKDATA_PERIOD : Long = 60 * 15 * 1000
    const val CHECK_CHECKDATA_ALARM_PERIOD : Long = 60 * 30 * 1000

    const val STORAGE_PERMISSION_REQUEST_CODE = 13
    const val CHECKDATA_ALARM_REQUEST_CODE = -1
    const val CHECK_CHECKDATA_ALARM_REQUEST_CODE = -2

}