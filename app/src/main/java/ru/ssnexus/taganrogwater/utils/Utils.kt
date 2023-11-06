package ru.ssnexus.taganrogwater.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build

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
}