package com.mylocations.utils

import android.location.Geocoder

/**
 * Utils class for constants, common functions
 */
object LocationUtils {
    const val EXTRA_ADDRESS = "address"
    const val EXTRA_LOCATION_ID = "id"
    const val APP_PREFERENCE = "com.mylocations"
    const val FIRST_TIME = "first_time"
    const val LAUNCH_DATA_RETRIEVED = "yes"
    const val LAUNCH_DATA_NOT_RETRIEVED = "no"
    const val LOCATIONS = "locations"

    const val LOCATION_NAME_ERROR = "Enter a name for the location"
    const val LOCATION_NOTES_ERROR = "What do you like about this place?"
    const val NOTES_UPDATED = "Notes updated"
    const val LOCATION_TYPE_DEFAULT = 1
    const val LOCATION_TYPE_CUSTOM = 2

    /**
     * Get string address from latitude and longitude
     * @param context
     * @param latitude
     * @param longitude
      */
    fun getAddressFromLatLng(geocoder: Geocoder?, latitude: Double, longitude: Double): String{
        val addressList = geocoder?.getFromLocation(latitude, longitude, 1)
<<<<<<< HEAD
        addressList?.let {
=======
        if (addressList != null && addressList.size > 0) {
>>>>>>> f519e2925f312af79c3fbdf5d80044e65be92f9f
            val address = addressList[0]
            val sb = StringBuffer()
            sb.append(if (address.getAddressLine(0) != null) address.getAddressLine(0) else "")
            //sb.append(if (address.countryName != null) address.countryName else "")
            return sb.toString()
        }
<<<<<<< HEAD

=======
>>>>>>> f519e2925f312af79c3fbdf5d80044e65be92f9f
        return "Address not updated"
    }

    /**
     * Returns distance in string format.
     * If the distance is less than 1000, it is returned as meters.
     * Else, the distance is converted to Kms.
     * @param distance
     */
    fun getDistanceString(distance: Float) : String{
        var distanceStr: String
        if (distance >= 1000){
            val temp = distance/1000
            distanceStr = temp.toString()
            if (distanceStr.contains(".")){
                distanceStr = distanceStr.substring(0, distanceStr.indexOf(".") + 2)
            }
            distanceStr = distanceStr.plus(" km")
        }else{
            distanceStr = distance.toString()
            if (distanceStr.contains(".")){
                distanceStr = distanceStr.substring(0, distanceStr.indexOf(".") + 2)
            }
            distanceStr = distanceStr.plus(" m")
        }
        return distanceStr
    }
}
