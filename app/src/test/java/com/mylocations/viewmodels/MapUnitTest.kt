package com.mylocations.viewmodels

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.util.Log
import com.mylocations.dao.Db
import com.mylocations.extensions.getValueBlocking
import com.mylocations.ui.viewmodel.MapViewModel
import com.mylocations.network.DataHandler
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MapUnitTest : Db() {
    private lateinit var viewModel: MapViewModel

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Before
    fun create(){
        val repository = DataHandler(localRepository, networkHandler)
        viewModel = MapViewModel()
        viewModel.dataHandler = repository
    }

    @Test
    fun appStartTest(){
        viewModel.appStart()
        var countLiveData = viewModel.getItemCount()
        var countFromDb = countLiveData?.getValueBlocking()
        Log.d("map", countFromDb.toString())
        assertEquals(countFromDb, 5.toString())
        viewModel.appStart()
        countLiveData = viewModel.getItemCount()
        countFromDb = countLiveData?.getValueBlocking()
        assertEquals(countFromDb, 5.toString())
    }
}