package com.mylocations.ui.model

import com.mylocations.persistence.models.CustomLocation

/**
 * Data class for Location List item
 * @param location - Location object
 * @param distance - distance from the current location
 * @param distanceString - distance in String format
 */
class LocationListItem(var location: CustomLocation, var distance: Float, var distanceString: String = "")