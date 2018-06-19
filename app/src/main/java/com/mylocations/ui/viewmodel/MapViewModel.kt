package com.mylocations.ui.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import android.location.Address
import com.mylocations.Application
import com.mylocations.network.OnDataReadyCallback
import com.mylocations.network.DataHandler
import com.mylocations.persistence.models.CustomLocation
import com.mylocations.utils.LocationUtils

/**
 * View model class for Map View
 */
class MapViewModel : ViewModel() {
    //Communicates with the local and remote dataHandler
    var dataHandler: DataHandler = Application.getRepositoryInstance()

    // LiveData variables observed in the activity
    val isLoading = MutableLiveData<Boolean>()
    val listItemLive = MutableLiveData<Boolean>()
    val selectLocationLive = MutableLiveData<Boolean>()
    val isSelectingLive = MutableLiveData<Boolean>()

    //Observable fields for data binding
    val isSelecting = ObservableField<Boolean>(false)
    val markedText = ObservableField<String>()
    val itemCount = ObservableField<String>()
    val address = ObservableField<Address>()

    init {
        isLoading.value = false
        isSelectingLive.value = false
        listItemLive.value = false;
        selectLocationLive.value = false;
    }

    /**
     * Gets the list of locations from the database. This value is observed in the view.
     */
    fun getLocationList(): LiveData<List<CustomLocation>>? =  dataHandler.getAllLocations()

    /**
     * Called at the app launch. If this is the first time launch, default locations are imported
     * from the json file stored in assets and inserted to the database. Else, do nothing.
     */
    fun appStart(){
        val firstTime = dataHandler.getPreference(LocationUtils.FIRST_TIME);
        if (firstTime == null || firstTime.equals(LocationUtils.LAUNCH_DATA_NOT_RETRIEVED)){
            isLoading.value = true
            dataHandler.getLocationsFromFile(object: OnDataReadyCallback {
                override fun sendStatus(status: Boolean) {
                    isLoading.postValue(false)
                }
            })
        }
    }

    /**
     * Gets the number of locations from the database. This value is observed in the view.
     */
    fun getItemCount(): LiveData<String>? = dataHandler.getItemCount()

    /**
     * Click function for displaying the list of locations.
     */
    fun listItems(){
        listItemLive.value = true
    }

    /**
     * Click function for add the location.
     */
    fun selectLocation(){
        markedText.set("")
        isSelectingLive.value = false
        isSelecting.set(false)
        selectLocationLive.value = true
    }

    /**
     * Click function for the floating action button.
     * Switches the visibility of Add location layout and List items layout.
     */
    fun fabClicked(){
        markedText.set("")
        isSelectingLive.value = isSelecting.get()?.not()
        isSelecting.set(isSelecting.get()?.not())
    }
}