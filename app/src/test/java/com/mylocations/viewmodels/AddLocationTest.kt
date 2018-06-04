package com.mylocations.viewmodels

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.location.Address
import com.mylocations.ui.viewmodel.AddLocationViewModel
import com.mylocations.dao.Db
import com.mylocations.extensions.getValueBlocking
import com.mylocations.network.DataHandler
import com.mylocations.utils.LocationUtils
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

@RunWith(RobolectricTestRunner::class)
class AddLocationTest : Db(){

    private lateinit var viewModel: AddLocationViewModel

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Before
    fun create(){
        val repository = DataHandler(localRepository, networkHandler)
        viewModel = AddLocationViewModel()
        viewModel.dataHandler = repository
    }

    @Test
    fun saveLocationTest(){
        viewModel.saveLocation()
        assertEquals(viewModel.nameError.value, LocationUtils.LOCATION_NAME_ERROR)
        viewModel.name.set("Sydney")
        viewModel.notes.set("Sydney notes")
        val address = Address(Locale.getDefault())
        address.latitude = 13.000
        address.longitude = 20.111
        viewModel.setAddress(address)
        viewModel.saveLocation()
        val locationLiveData = appDatabase.localLocationModel().getLocation("1")
        val locationFromDb = locationLiveData.getValueBlocking()
        assertEquals(locationFromDb?.locationName, viewModel.name.get())
    }
}