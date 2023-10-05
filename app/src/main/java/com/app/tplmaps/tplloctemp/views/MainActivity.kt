package com.app.tplmaps.tplloctemp.views

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.tplmaps.tplloctemp.adapter.LocationAdapter
import com.app.tplmaps.tplloctemp.databinding.ActivityMainBinding
import com.app.tplmaps.tplloctemp.db.database.LocationsDatabase
import com.app.tplmaps.tplloctemp.db.model.POI
import com.app.tplmaps.tplloctemp.db.repo.LocationRepository
import com.app.tplmaps.tplloctemp.db.viewModel.LocationViewModel
import com.app.tplmaps.tplloctemp.db.viewModel.LocationVmProviderFactory
import com.app.tplmaps.tplloctemp.utils.LocationEvent
import com.app.tplmaps.tplloctemp.utils.LocationService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding
        get() = _binding!!


    private var service: Intent?=null

    private val backgroundLocation =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {

            }
        }
    private lateinit var locationViewModel: LocationViewModel

    private lateinit var locationAdapter: LocationAdapter

    private val locationPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when {
                it.getOrDefault(android.Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (ActivityCompat.checkSelfPermission(
                                this,
                                android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            backgroundLocation.launch(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        }
                    }

                }
                it.getOrDefault(android.Manifest.permission.ACCESS_FINE_LOCATION, false) -> {

                }
            }
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = Intent(this,LocationService::class.java)

        initResultsViewModel()



        binding.btnStart.setOnClickListener{
            checkPermissions()

        }

        binding.btnStop.setOnClickListener{
            stopService(service)
            isFromStop=true
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

    override fun onStart() {
        super.onStart()
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this)
        }
    }

    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }else{
                startService(service)
            }
        }
    }
    override fun onDestroy() {
        stopService(service)
        if(EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this)
        }
        super.onDestroy()
    }

    @Subscribe
    fun receiveLocationEvent(locationEvent: LocationEvent){
        binding.latTextView.text = "Latitude -> ${locationEvent.latitude}"
        binding.lngTextView.text = "Longitude -> ${locationEvent.longitude}"

        val poi=POI(0,locationEvent.longitude.toString(),locationEvent.latitude.toString())
        locationViewModel.addLocation(poi)
    }
    companion object{
        var isFromStop=false
    }
}

