package com.mylocations.persistence

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.support.test.runner.AndroidJUnit4
import com.mylocations.dao.Db
import com.mylocations.extensions.getValueBlocking
import com.mylocations.network.OnDataReadyCallback
import com.mylocations.network.DataHandler
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertEquals

@RunWith(AndroidJUnit4::class)
class DataHandlerTest : Db() {

    private lateinit var dataHandler: DataHandler

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun create(){
        dataHandler = DataHandler(localRepository, networkHandler)
    }

    @Test
    fun getLocationFromFileTest(){
        dataHandler.getLocationsFromFile(object : OnDataReadyCallback {
            override fun sendStatus(status: Boolean) {

            }
        })
        val locationsLiveData = appDatabase.localLocationModel().getAllLocations()
        val locationsFromDb = locationsLiveData.getValueBlocking()
        assertEquals(locationsFromDb?.size, 5)
    }

}