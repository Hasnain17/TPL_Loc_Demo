package com.app.tplmaps.tplloctemp.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tplmaps.tplloctemp.R
import com.app.tplmaps.tplloctemp.adapter.LocationAdapter
import com.app.tplmaps.tplloctemp.databinding.ActivityMainBinding
import com.app.tplmaps.tplloctemp.db.database.LocationsDatabase
import com.app.tplmaps.tplloctemp.db.model.POI
import com.app.tplmaps.tplloctemp.db.repo.LocationRepository
import com.app.tplmaps.tplloctemp.db.viewModel.LocationViewModel
import com.app.tplmaps.tplloctemp.db.viewModel.LocationVmProviderFactory
import com.app.tplmaps.tplloctemp.utils.PermissionUtils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices


class MainActivity : AppCompatActivity() {
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter
    private lateinit var looper: Looper

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient


    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initResultsViewModel()
        binding.btnStop.setOnClickListener{
            if (fusedLocationProviderClient!=null){

            }
        }

        binding.btnShow.setOnClickListener {
            locationAdapter= LocationAdapter(emptyList())

            binding.rvLocations.adapter=locationAdapter
            binding.rvLocations.layoutManager = LinearLayoutManager(this)
            locationViewModel.getAllLocation().observe(this
            ) { value ->
                if (value != null) {
                    binding.rvLocations.visibility = View.VISIBLE
                    locationAdapter.updateData(value)
                }
            }
        }
    }

    private fun initResultsViewModel() {
        val resultRepository= LocationRepository(LocationsDatabase(this@MainActivity))
        val viewModProviderFactory= LocationVmProviderFactory(app = application,resultRepository)
        locationViewModel= ViewModelProvider(this,viewModProviderFactory)[LocationViewModel::class.java]
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setUpLocationListener() {
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this)
        // for getting the current location update after every 5 seconds with high accuracy
        val locationRequest = com.google.android.gms.location.LocationRequest().setInterval(5000).setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        binding.latTextView.text = location.latitude.toString()
                        binding.lngTextView.text = location.longitude.toString()
                        val poi=POI(0,location.longitude.toString(),location.latitude.toString())
                        locationViewModel.addLocation(poi)
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },Looper.myLooper()
        )
    }
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        binding.btnStart.setOnClickListener{
                            setUpLocationListener()
                        }
                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
//                            setUpLocationListener()
                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}

