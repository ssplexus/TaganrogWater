package ru.ssnexus.taganrogwater

object AppConstants {
    const val DB_VERSION = 2
    const val DB_NAME = "notifications_db"
    const val DATA_URL = "http://www.tgnvoda.ru/avarii.php"
    const val CONTACTS_URL = "http://www.tgnvoda.ru/kontakt.html"

    const val NOTIFICATION_ID = 1
    const val CHANNEL_ID = "channel1"
    const val CHANNEL_NAME = "notif channel"
    const val CHANNEL_DESCR = "A Description of the Channel"
    const val TITLE_EXTRA = "titleExtra"
    const val MESSAGE_EXTRA = "messaeExtra"

    const val ACTION_CHECKDATA = "CheckData"

    const val CHECKDATA_PERIOD : Long = 60000 * 5

}