package com.mylocations.network

import android.location.Geocoder
import com.mylocations.network.model.ResponseModel
import com.mylocations.persistence.local.LocalRepository
import com.mylocations.persistence.models.CustomLocation
import com.mylocations.utils.LocationUtils

/**
 * DataHandler acts as a medium between view model and data.
 * Communicates with Local/Remote dataHandler to get the data.
 */
class DataHandler(val localRepository: LocalRepository?, val networkHandler: NetworkHandler?, val geoCoder: Geocoder? = null) {

    /**
     * Inserts the location in the local database
      */
    fun addLocation(customLocation: CustomLocation) {
        localRepository?.addLocation(customLocation)
    }

    /**
     * Gets all the locations from the database. This value is observed.
     */
    fun getAllLocations() = localRepository?.getAllLocations()

    /**
     * Gets location based on id from the database. This value is observed.
     */
    fun getLocation(id: String) = localRepository?.getLocation(id);

    /**
     * Update notes for the location in the local dataHandler.
     */
    fun updateNotes(id: String?, notes: String?){
        localRepository?.updateNotes(id, notes)
    }

    /**
     * Called on first app launch to import default locations from JSON files and insert into database.
     * Once done, notifies the called through callback interface method. Remote dataHandler is used for easier management in future changes.
     */
    fun getLocationsFromFile(onDataReadyCallback: OnDataReadyCallback){
        networkHandler?.getLocationsFromFile(object : OnRemoteDataReadyCallback {
            override fun onRemoteDataReady(responseModel: ResponseModel) {
                val list = ArrayList<CustomLocation>()
                for (item in responseModel.locations){
                    val address = LocationUtils.getAddressFromLatLng(geoCoder, item.lat, item.lng)
                    list.add(CustomLocation(item.name, "Notes: ${item.name}", item.lat, item.lng, address, LocationUtils.LOCATION_TYPE_DEFAULT))
                }
                localRepository?.addLocations(list)
                putPreference(LocationUtils.FIRST_TIME, LocationUtils.LAUNCH_DATA_RETRIEVED)
                onDataReadyCallback.sendStatus(true)
            }
        })
    }

    // Method the get the value from shared preference
    fun getPreference(key: String) = localRepository?.getData(key)

    // Method to put the value to shared preference
    fun putPreference(key: String, value: String) = localRepository?.putData(key, value)

    //Gets the total number of locations. This value is observed.
    fun getItemCount() = localRepository?.getItemCount()


}

/**
 * Callback interface to notify the calling class after inserting the locations in database
 */
interface OnDataReadyCallback{
    fun sendStatus(status: Boolean)
}
