package com.mylocations.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.Typeface
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mylocations.R
import com.mylocations.databinding.ActivityMapsBinding
import com.mylocations.persistence.models.CustomLocation
import com.mylocations.ui.viewmodel.MapViewModel
import com.mylocations.utils.LocationUtils
import com.mylocations.utils.NetworkUtils
import java.util.*

/**
 * Maps activity to display the markers - both default and custom locations.
 * The user can add a custom location by placing a marker in the map.
 */
class MapsActivity : BaseActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {
    private val LOCATION_REQUEST = 1
    private var mLocationAccess = false
    private lateinit var gMap: GoogleMap
    private var mLastKnownLocation: Location? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mMapMarker: Marker? = null
    private lateinit var mapParameters: MapViewModel
    private lateinit var mMapBinding: ActivityMapsBinding
    private lateinit var mLocalLocation: LatLng

    private var _locationListTitle: TextView? = null
    private var _fontAllen: Typeface? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeView()
        setFont();
        initializeFragment()
        initializeLocationClient()
        initializeViewModel()
        setToolBar()
        setStaticLocation();
    }

    private fun setFont() {
        _locationListTitle!!.setTypeface(this._fontAllen, Typeface.NORMAL)
    }

    private fun setStaticLocation() {
        mLocalLocation = LatLng(-34.0, 151.0)
    }

    private fun setToolBar() {
        setSupportActionBar(mMapBinding.toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setTitle(getString(R.string.app_name))

    }

    private fun initializeViewModel() {
        mapParameters = ViewModelProviders.of(this).get(MapViewModel::class.java)
        mapParameters.getLocationList()!!.observe(this, Observer { locations -> addMarkersToMap(locations!!) })
        mapParameters.isLoading.observe(this, Observer { status -> if (status == true) showProgress() else closeProgress() })
        mapParameters.isSelectingLive.observe(this, Observer { status -> showOrHideMarker(status) })
        mapParameters.getItemCount()!!.observe(this, Observer { count -> mapParameters.itemCount.set(if (count != null) "${this.getString(R.string.location_list)}" else "${this.getString(R.string.no_location_found)}") })
        mapParameters.listItemLive.observe(this, Observer { status -> if (status == true) startListActivity() })
        mapParameters.selectLocationLive.observe(this, Observer { status -> if (status == true) startAddLocationActivity() })
        mapParameters.appStart();
        mMapBinding.viewModel = mapParameters
    }

    private fun initializeLocationClient() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    private fun initializeView() {
        mMapBinding = DataBindingUtil.setContentView(this, R.layout.activity_maps)
        _locationListTitle = this.findViewById(R.id.item_count)
        _fontAllen = ResourcesCompat.getFont(this.applicationContext, R.font.aller_italic_lite)

    }


    private fun initializeFragment() {

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Called when the user clicks on the floating action button.
     * If the operation is Add Location, a new marker is created and placed on the map.
     * @param showMarker
     */
    private fun showOrHideMarker(showMarker: Boolean?) {
        if (showMarker!!) {
            mMapBinding.selectLocation.animate().alpha(1.0f)
            mMapBinding.listItems.animate().alpha(0.0f)
            val latLng: LatLng
            if (mLastKnownLocation == null) {
                latLng = mLocalLocation
            } else {
                latLng = LatLng(mLastKnownLocation!!.latitude, mLastKnownLocation!!.longitude)
            }
            if (::gMap.isInitialized) {
                mMapMarker = gMap.addMarker(MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_flag)).alpha(0.8f).zIndex(100f).draggable(true))
                mMapMarker!!.tag = -1
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                setMarkerDetails(latLng)
            }
            showToast(getString(R.string.drop_pin))
        } else {
            mMapBinding.selectLocation.animate().alpha(0.0f)
            mMapBinding.listItems.animate().alpha(1.0f)
            if (mMapMarker != null) mMapMarker!!.remove()
        }
    }


    /**
     * Manipulates the map once available.LatLn
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.setOnMarkerDragListener(this)
        gMap.setOnMarkerClickListener(this)
        checkLocationPermission()
        updateLocationUI()
        if (NetworkUtils(applicationContext).isConnected!!.not()) {
            showToast(getString(R.string.network_connection))
        }
    }

    /**
     * Check whether location permission is granted. Else, request from the user,
     */
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationAccess = true
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST -> {
                mLocationAccess = false
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationAccess = true
                    //get the current location if location permission has been granted
                    getCurrentLocation()
                }
                updateLocationUI()
            }
        }
    }

    /**
     * Display my location icon in the map based on the permissions
     */

    private fun updateLocationUI() {

        try {
            if (mLocationAccess) {
                gMap.isMyLocationEnabled = true
                gMap.uiSettings.isMyLocationButtonEnabled = true
            } else {
                gMap.isMyLocationEnabled = false
                gMap.uiSettings.isMyLocationButtonEnabled = false
                mLastKnownLocation = null

            }

        } catch (e: SecurityException) {

        }

    }

    private fun getCurrentLocation() {
        try {
            if (mLocationAccess) {
                val locationResult = mFusedLocationClient!!.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //Store the current location
                        mLastKnownLocation = task.result
                    }
                }
            }
        } catch (e: SecurityException) {

        }

    }

    /**
     * Start list activity passing the current location as an extra to calculate the distance
     */
    private fun startListActivity() {
        mapParameters.listItemLive.value = false
        mLastKnownLocation?.let {
            val intent = Intent(this, LocationListActivity::class.java)
            intent.putExtra(LocationUtils.EXTRA_ADDRESS, mLastKnownLocation)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } ?: run {
            checkLocationPermission()
        }
    }


    /**
     * Start add location activity passing the Address object which contains latitude and longitude
     */
    private fun startAddLocationActivity() {
        mapParameters.selectLocationLive.value = false
        mMapBinding.markedLocation.tag?.let {
            val intent = Intent(this, AddLocationActivity::class.java)
            intent.putExtra(LocationUtils.EXTRA_ADDRESS, mMapBinding.markedLocation.tag as Address)
            mapParameters.address.set(null)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } ?: run {
            showToast(getString(R.string.something_wrong))
        }

    }

    override fun onMarkerDragStart(marker: Marker) {

    }

    override fun onMarkerDrag(marker: Marker) {

    }

    /**
     * Callback available once the user drop the marker
     */


    override fun onMarkerDragEnd(marker: Marker) {

        catchAll(LOG, "Marker Position Changed") {
            val latLng = marker.position
            setMarkerDetails(latLng)
        }



    }



    /**
     * Update the text and address of the draggable marker dropped by user.
     * This function calculates the address from the latitude and longitude of the marker position.
     * @param latLng
     */
    private fun setMarkerDetails(latLng: LatLng) {
        catchAll(LOG, "Set Marker") {
            val geocoder = Geocoder(applicationContext, Locale.getDefault())
            val addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addressList?.let {
                val address = addressList[0]
                val sb = StringBuffer()
                sb.append(if (address.getAddressLine(0) != null) address.getAddressLine(0) else "")
                mapParameters.markedText.set(sb.toString())
                mapParameters.address.set(address)
            } ?: run {
                mapParameters.markedText.set("")
                mapParameters.address.set(null)
            }

        }

    }

    /**
     * Called after the locations are retrieved (by observing for changes) from the database.
     * @param locations
     */
    private fun addMarkersToMap(locations: List<CustomLocation>) {
        gMap.clear()
        for (location in locations) {
            val marker = gMap.addMarker(MarkerOptions().position(LatLng(location.latitude, location.longitude)).title(location.locationName))
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(if (location.type == LocationUtils.LOCATION_TYPE_DEFAULT) BitmapDescriptorFactory.HUE_RED else BitmapDescriptorFactory.HUE_MAGENTA))
            marker.tag = location.id
        }
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLocalLocation, 10.0f))
    }

    /**
     * This callback is available once the user clicks on a marker.
     * Based on the tag, associated with the marker, its details are shown to the user.
     */
    override fun onMarkerClick(marker: Marker?): Boolean {
        val id = marker?.tag as Int
        if (id == -1) return false
        startLocationDetailActivity(id)
        return true;
    }
}
