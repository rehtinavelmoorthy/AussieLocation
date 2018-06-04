package com.mylocations.utils

import android.content.Context
import android.net.ConnectivityManager

/**
 * Manager class to provide information about network
 */
class NetworkUtils(val context: Context) {

    // Return whether the device is connected to the internet or not
    val isConnected: Boolean?
        get(){
            val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectionManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
}