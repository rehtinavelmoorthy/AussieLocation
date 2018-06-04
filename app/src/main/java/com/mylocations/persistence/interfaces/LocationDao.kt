package com.mylocations.persistence.interfaces

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import com.mylocations.persistence.models.CustomLocation

/**
 * DAO class for Location entity. Contains queries for the table.
 */

@Dao
interface LocationDao {

    @Query("Select * from CustomLocation")
    fun getAllLocations(): LiveData<List<CustomLocation>>

    @Query("Select * from CustomLocation where id = :id")
    fun getLocation(id: String): LiveData<CustomLocation>

    @Insert(onConflict = REPLACE)
    fun addLocation(customLocation: CustomLocation) : Long

    @Insert(onConflict = REPLACE)
    fun addLocations(customLocations: List<CustomLocation>)

    @Delete
    fun deleteLocation(customLocation: CustomLocation)

    @Query("Update CustomLocation set locationNotes = :notes where id = :id")
    fun updateNotes(id: String?, notes: String?)

    @Query("Select count(*) from CustomLocation")
    fun getCount(): LiveData<String>
}