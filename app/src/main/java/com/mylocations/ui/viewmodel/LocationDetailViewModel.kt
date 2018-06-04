package com.mylocations.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import com.mylocations.Application
import com.mylocations.network.DataHandler
import com.mylocations.persistence.models.CustomLocation
import com.mylocations.utils.LocationUtils

/**
 * View model class for Location Detail
 */
class LocationDetailViewModel : ViewModel() {

    //Communicates with the local and remote dataHandler
    var dataHandler: DataHandler = Application.getRepositoryInstance()

    //Observable fields for data binding
    val notes = ObservableField<String>()
    val location = ObservableField<CustomLocation>()
    val editing = ObservableField<Boolean>(false)

    // LiveData variables observed in the activity
    val notesUpdated = MutableLiveData<String>()
    /**
     * Gets the location from the database. This value is observed in the view
     */
    fun getLocation(id: String): LiveData<CustomLocation>? =  dataHandler.getLocation(id);

    /**
     * Called when the user edits the notes of the location.
     * Updates the database and notifies the view.
     */
    fun updateNotes(){
        dataHandler.updateNotes(location.get()?.id.toString(), notes.get())
        editing.set(false)
        notesUpdated.value = LocationUtils.NOTES_UPDATED
    }

    // Databinding for notes edittext
    fun setNotes(notes: String){
        this.notes.set(notes)
    }

    // Called on clicking the edit icon
    fun startEdit(){
        editing.set(true)
    }

    // Called on clicking the close icon
    fun stopEdit(){
        editing.set(false)
    }

    // Sets the details of the location to be updated in the view
    fun setDetails(location: CustomLocation){
        this.location.set(location)
        notes.set(location.locationNotes)
    }
}