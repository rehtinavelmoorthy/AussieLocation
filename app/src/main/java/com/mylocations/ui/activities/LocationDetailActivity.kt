package com.mylocations.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.mylocations.R
import com.mylocations.databinding.ActivityLocationDetailBinding
import com.mylocations.ui.viewmodel.LocationDetailViewModel
import com.mylocations.utils.LocationUtils

/**
 * Detailed view of the location.
 */
class LocationDetailActivity : BaseActivity() {

    private lateinit var locationDetailBinding: ActivityLocationDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
        initializeViewModel()
        setActionBar()
    }

    private fun setActionBar() {
        setSupportActionBar(locationDetailBinding.toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.location_detail))
    }

    private fun initializeViewModel() {
        val viewModel = ViewModelProviders.of(this).get(LocationDetailViewModel::class.java)
        val id = intent.getIntExtra(LocationUtils.EXTRA_LOCATION_ID, 0).toString()
        viewModel.getLocation(id)!!.observe(this, Observer {
            it?.let {
                viewModel.setDetails(it)
            }
        })
        viewModel.notesUpdated.observe(this, Observer { value -> Toast.makeText(this, value, Toast.LENGTH_SHORT).show() })
        locationDetailBinding.viewModel = viewModel
    }

    private fun initializeView() {
        locationDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_location_detail)
    }
}