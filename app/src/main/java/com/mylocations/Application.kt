package com.mylocations

import android.app.Application
import android.location.Geocoder
import com.mylocations.network.DataHandler
import com.mylocations.network.NetworkHandlerSingleton
import com.mylocations.persistence.local.LocalSingleton

/**
 * Custom application class to provide a global instance for DataHandler
 */
class Application : Application(){

    companion object {
        private lateinit var dataHandler: DataHandler
        fun getRepositoryInstance() = dataHandler
    }

    override fun onCreate() {
        super.onCreate()
        dataHandler = DataHandler(LocalSingleton.getInstance(applicationContext), NetworkHandlerSingleton.getInstance(applicationContext), Geocoder(applicationContext))
    }
}