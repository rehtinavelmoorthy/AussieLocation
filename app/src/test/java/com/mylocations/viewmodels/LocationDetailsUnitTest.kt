package com.mylocations.viewmodels

import com.mylocations.ui.viewmodel.LocationDetailViewModel
import com.mylocations.network.DataHandler
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import android.arch.core.executor.testing.InstantTaskExecutorRule
import com.mylocations.dao.Db
import com.mylocations.extensions.getValueBlocking
import com.mylocations.persistence.models.CustomLocation
import com.mylocations.utils.LocationUtils
import org.junit.Rule
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationDetailsUnitTest : Db(){

    private lateinit var viewModel : LocationDetailViewModel

    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Before
    fun create(){
        val repository = DataHandler(localRepository, networkHandler)
        viewModel = LocationDetailViewModel()
        viewModel.dataHandler = repository
    }

    @Test
    fun updateNotes_Test(){
        val location = insertLocation()
        viewModel.notes.set("Sydney Opera House")
        viewModel.location.set(location)
        viewModel.updateNotes()
        //val locationLiveData = appDatabase.localLocationModel().getLocation(location?.id.toString())
        val locationLiveData = viewModel.getLocation(location?.id.toString())
        val locationFromDb = locationLiveData?.getValueBlocking()
        assertEquals(locationFromDb?.locationNotes, "Sydney Opera House")
        assertEquals("Notes updated", viewModel.notesUpdated.value)
        assertEquals(false, viewModel.editing.get())
    }

    fun insertLocation() : CustomLocation?{
        val location = CustomLocation("Opera house", "Opera house", 13.4323, 87.123, "Sydeny opera house, NSW, Australia", LocationUtils.LOCATION_TYPE_DEFAULT)
        val id = appDatabase.localLocationModel().addLocation(location)
        val locationLiveData = viewModel.getLocation(id.toString())
        val locationFromDb = locationLiveData?.getValueBlocking()
        return locationFromDb
    }

}