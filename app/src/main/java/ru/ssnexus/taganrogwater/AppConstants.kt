package ru.ssnexus.taganrogwater

object AppConstants {
    const val VERSION = 2
    const val GET_DATA_JOB_ID = 1
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

    const val CHECKDATA_PERIOD : Long = 60 * 10 * 1000
//    const val CHECKDATA_PERIOD : Long = 8000

    const val GETDATA_WORKER_NAME = "get_data_worker_name"
    const val STORAGE_PERMISSION_REQUEST_CODE = 13
    const val LOG_FILE_PATH = ""

}