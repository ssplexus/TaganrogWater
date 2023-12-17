package ru.ssnexus.taganrogwater

object AppConstants {
    const val VERSION = 1
    const val DB_VERSION = 3
    const val DB_NAME = "notifications_db"
    const val DATA_URL = "http://www.tgnvoda.ru/avarii.php"
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

//    const val CHECKDATA_PERIOD : Long = 60000 * 5
    const val CHECKDATA_PERIOD : Long = 10000



}