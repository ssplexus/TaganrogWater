package ru.ssnexus.taganrogwater.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.Window
import android.widget.*
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import ru.ssnexus.taganrogwater.App
import ru.ssnexus.taganrogwater.R
import ru.ssnexus.taganrogwater.activity.AboutActivity
import ru.ssnexus.taganrogwater.activity.DetailsActivity
import ru.ssnexus.taganrogwater.activity.MainActivity
import timber.log.Timber
import java.io.IOException

object Utils {

    fun checkConnection(context: Context): Boolean {
        val activity = context as Activity
        val connectivityManager = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >  Build.VERSION_CODES.Q){
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null){
                if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                    return true
                } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                    return true
                } else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                    return true
                }
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return this.isConnectedOrConnecting
            }
        }
        return false
    }

    fun showSettingsDialog(context: Context, dismissListener: DialogInterface.OnDismissListener) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.settings_dialog_layout)

        val periodSpinner = dialog.findViewById<Spinner>(R.id.periodSpinner)
        val everydayRadioBtn = dialog.findViewById<RadioButton>(R.id.everydayRadioBtn)

        val radioGr = dialog.findViewById<RadioGroup>(R.id.periodRadioGr)
        val okBtn = dialog.findViewById<Button>(R.id.okBtn)
        val cancelBtn = dialog.findViewById<ImageButton>(R.id.cancelBtn)

        radioGr.setOnCheckedChangeListener { radioGroup, checked ->
            when (checked) {
                R.id.everyHourRadioBtn -> {
                    periodSpinner.isEnabled = true
                }
                R.id.everydayRadioBtn -> {
                    periodSpinner.isEnabled = false
                }
            }
        }
        everydayRadioBtn.isChecked = true

        okBtn.setOnClickListener {
            var period = 24
            if (!everydayRadioBtn.isChecked) when (periodSpinner.selectedItemPosition) {
                0 -> period = 12
                1 -> period = 8
                else -> period = 4
            }
            DetailsActivity.notificationMarked = period
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setOnDismissListener(dismissListener)
        dialog.show()
    }

    // Сравнивает две строки формата ddmmyy
    @Throws(IOException::class, NumberFormatException::class)
    fun String.strDateCompare(_cmpDate : String):Int {
        val srcDateList = this.split(".")
        val cmpDateList = _cmpDate.split(".")
        if(srcDateList.size != 3 || cmpDateList.size != 3)
            throw IOException()
        val cmpDay = cmpDateList[0].toInt()
        val cmpMonth = cmpDateList[1].toInt()
        val cmpYear = cmpDateList[2].toInt()
        val srcDay = srcDateList[0].toInt()
        val srcMonth = srcDateList[1].toInt()
        val srcYear = srcDateList[2].toInt()

        if(srcYear > cmpYear) return 1
        else if (srcYear < cmpYear) return -1
        if(srcMonth > cmpMonth) return 1
        else if (srcMonth < cmpMonth) return -1
        if(srcDay > cmpDay) return 1
        else if (srcDay < cmpDay) return -1

        return 0
    }

    fun getFireBaseRemoteConfig(activity: MainActivity){
        if(AboutActivity.author_text.isEmpty())
                AboutActivity.author_text = activity.resources.getText(R.string.author_str).toString()
        // Получение параметра из Remote Config
        val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val mFirebaseRemoteConfigSettings = FirebaseRemoteConfigSettings.Builder().build()
        mFirebaseRemoteConfig.reset()
        mFirebaseRemoteConfig.setConfigSettingsAsync(mFirebaseRemoteConfigSettings).addOnCompleteListener { _ ->
            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    val fbValEmail = mFirebaseRemoteConfig.getString("email_val")
                    val fbValSiteUrl = mFirebaseRemoteConfig.getString("site_url")
                    val fbValSiteContactsUrl = mFirebaseRemoteConfig.getString("site_contacts_url")
                    Timber.i("MainActivity FB Result: Fetch config success! $fbValEmail")
                    Timber.i("MainActivity FB Result: Fetch config success! $fbValSiteUrl")
                    Timber.i("MainActivity FB Result: Fetch config success! $fbValSiteContactsUrl")
                    AboutActivity.author_text = activity.resources.getText(R.string.author_str).toString() + "\n" + fbValEmail
                    if(fbValSiteUrl.isNotEmpty()) App.instance.interactor.setSiteUrlPref(fbValSiteUrl)
                    if(fbValSiteContactsUrl.isNotEmpty()) App.instance.interactor.setSiteContactsUrlPref(fbValSiteContactsUrl)
                }
            }
        }
    }
}
//                val randId = random.nextInt(9999 - 1000) + 1000
//                val randId = (Date().time / 1000L % Int.MAX_VALUE).toInt()
