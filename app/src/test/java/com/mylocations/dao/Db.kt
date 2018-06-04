package com.mylocations.dao

import android.arch.persistence.room.Room
import android.content.Context
import android.content.SharedPreferences
import android.support.test.InstrumentationRegistry
import com.mylocations.persistence.local.AppDatabase
import com.mylocations.persistence.local.LocalRepository
import com.mylocations.network.NetworkHandler
import com.mylocations.utils.LocationUtils
import org.junit.After
import org.junit.Before
import org.robolectric.RuntimeEnvironment

abstract class Db {

    lateinit var appDatabase: AppDatabase
    var localRepository: LocalRepository? = null
    var networkHandler: NetworkHandler? = null
    lateinit var preferences: SharedPreferences

    @Before
    fun setup(){
        appDatabase = Room.inMemoryDatabaseBuilder(RuntimeEnvironment.application.applicationContext, AppDatabase::class.java).allowMainThreadQueries().build()
        preferences = InstrumentationRegistry.getContext().getSharedPreferences(LocationUtils.APP_PREFERENCE, Context.MODE_PRIVATE)
        localRepository = LocalRepository(appDatabase, preferences)
        networkHandler = NetworkHandler(InstrumentationRegistry.getContext().assets)
    }

    @After
    fun close(){
        appDatabase.close()
    }
}