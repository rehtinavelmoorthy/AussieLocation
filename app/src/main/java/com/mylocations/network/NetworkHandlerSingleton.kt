package com.mylocations.network

import android.content.Context

/**
 * Singleton class to retrieve the global instance for NetworkHandler
 */
class NetworkHandlerSingleton {

    companion object {
        private var networkHandler: NetworkHandler? = null

        //Function to get the singleton instance of NetworkHandler
        fun getInstance(context : Context) : NetworkHandler?{
            if (networkHandler == null){
                synchronized(NetworkHandler::class){
                    networkHandler = NetworkHandler(context.assets);
                }
            }
            return networkHandler
        }
    }
}