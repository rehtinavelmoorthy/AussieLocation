package com.mylocations.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.location.Location
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import com.mylocations.R
import com.mylocations.databinding.ActivityLocationListBinding
import com.mylocations.ui.adapters.LocationListAdapter
import com.mylocations.ui.viewmodel.LocationListViewModel
import com.mylocations.utils.LocationUtils

/**
 * Activity to display the list of locations. The locations are sorted in the increasing order of
 * distance from the current location.
 */
class LocationListActivity : BaseActivity(), LocationListAdapter.OnItemClickListener {

    private lateinit var binding: ActivityLocationListBinding
    private val adapter = LocationListAdapter(arrayListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
        initializeLocationViewModel()
        setToolBar()

    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.app_name))
    }

    private fun initializeLocationViewModel() {
        val location = intent.getParcelableExtra<Location>(LocationUtils.EXTRA_ADDRESS)
        val viewModel = ViewModelProviders.of(this).get(LocationListViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        viewModel.location = location
        viewModel.loadLocations()!!.observe(this, Observer {
            it?.let {
                viewModel.createLocationList(it)
            }
        })
        viewModel.locationList.observe(this, Observer {
            it?.let {
                adapter.replaceData(it)
            }
        })

        //initiate the recyler view
        binding.listView.layoutManager = LinearLayoutManager(this)
        binding.listView.adapter = adapter
    }


    private fun initializeView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_list)
    }


    override fun onItemClick(id: Int) {
        startLocationDetailActivity(id)
    }
}