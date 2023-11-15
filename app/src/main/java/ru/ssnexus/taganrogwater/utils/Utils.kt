package ru.ssnexus.taganrogwater.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.view.Window
import android.widget.*
import ru.ssnexus.taganrogwater.R

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
            val activeNetworkInfo = connectivityManager.activeNetworkInfo as NetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected){
                return true
            }
        }
        return false
    }

    fun showSettingsDialog(context: Context){
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
            dialog.dismiss()
        }
        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}