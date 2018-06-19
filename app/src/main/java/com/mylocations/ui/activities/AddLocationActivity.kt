package com.mylocations.ui.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.location.Address
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.widget.Toast
import com.mylocations.R
import com.mylocations.databinding.ActivityAddLocationBinding
import com.mylocations.ui.viewmodel.AddLocationViewModel
import com.mylocations.utils.LocationUtils

/**
 * Activity to add a new location.
 */
class AddLocationActivity : BaseActivity() {
    private lateinit var addLocationBinding: ActivityAddLocationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeView()
        initializeViewModel()
        setHeaderActionBar()
    }

    private fun setHeaderActionBar() {
        setSupportActionBar(addLocationBinding.toolbar as Toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(getString(R.string.add_location_title))
    }

    private fun initializeViewModel() {
        val viewModel = ViewModelProviders.of(this).get(AddLocationViewModel::class.java)
        intent?.let {
            val address = intent.getParcelableExtra<Address>(LocationUtils.EXTRA_ADDRESS)
            viewModel.setAddress(address)
        }

        viewModel.isLocationAdded().observe(this, Observer { locationAdded ->
            if (locationAdded == true) {
                Toast.makeText(this, getString(R.string.location_added), Toast.LENGTH_SHORT).show()
                finish()
            }
        })
        viewModel.nameError.observe(this, Observer { error -> addLocationBinding.nameTextLayout.setError(error) })
        viewModel.notesError.observe(this, Observer { error -> addLocationBinding.notesTextLayout.setError(error) })
        addLocationBinding.viewModel = viewModel
    }

    private fun initializeView() {
        addLocationBinding = DataBindingUtil.setContentView<ActivityAddLocationBinding>(this, R.layout.activity_add_location)
    }
}