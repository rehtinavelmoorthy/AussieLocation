package com.mylocations.persistence.local

import android.content.Context
import com.mylocations.utils.LocationUtils

/**
 * Singleton class to retrieve the global instance for LocalRepository
 */
class LocalSingleton {

    companion object {
        private var localRepository : LocalRepository? = null

        /**
         * Function to get the singleton instance of LocalRepository
         */
        fun getInstance(context : Context) : LocalRepository?{
            if (localRepository == null){
                synchronized(LocalRepository::class){
                    localRepository = LocalRepository(AppDatabase.getInstance(context), context.getSharedPreferences(LocationUtils.APP_PREFERENCE, Context.MODE_PRIVATE));
                }
            }
            return localRepository
        }
    }
}