package com.app.tplmaps.tplloctemp.views

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.app.tplmaps.tplloctemp.MyApp
import com.app.tplmaps.tplloctemp.adapter.LocationAdapter
import com.app.tplmaps.tplloctemp.databinding.ActivityMainBinding
import com.app.tplmaps.tplloctemp.db.database.LocationsDatabase
import com.app.tplmaps.tplloctemp.db.repo.LocationRepository
import com.app.tplmaps.tplloctemp.db.viewModel.LocationViewModel
import com.app.tplmaps.tplloctemp.db.viewModel.LocationVmProviderFactory
import com.app.tplmaps.tplloctemp.utils.LocationEvent
import com.app.tplmaps.tplloctemp.utils.LocationService
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin


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
     lateinit var locationViewModel: LocationViewModel

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

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        service = Intent(this,LocationService::class.java)

        initResultsViewModel()
        if (isMyServiceRunning(LocationService::class.java))
        {
            binding.btnStop.visibility=View.VISIBLE
        }

        binding.btnStart.setOnClickListener{
            checkPermissions()
//            isFromStop=false
            hideViews()
        }

        binding.btnStop.setOnClickListener{
            stopService(service)
            isFromStop=true
            binding.btnStop.visibility=View.GONE
            binding.btnShow.visibility=View.VISIBLE
        }

        binding.btnShow.setOnClickListener {
    /*        locationAdapter= LocationAdapter(emptyList())

            binding.rvLocations.adapter=locationAdapter
            binding.rvLocations.layoutManager = LinearLayoutManager(this)
            locationViewModel.getAllLocation().observe(this
            ) { value ->
                if (value != null) {
                    binding.rvLocations.visibility = View.VISIBLE
                    locationAdapter.updateData(value)
                }
            }*/

            if (MyApp.prefs.exists("startLocLat")
                and MyApp.prefs.exists("startLocLong")
                and MyApp.prefs.exists("endLocLat")
                and MyApp.prefs.exists("endLocLong")
            ){
                showViews()
                val lt1=MyApp.prefs.pull<String>("startLocLat")
                val lt2=MyApp.prefs.pull<String>("endLocLat")
                val ld1=MyApp.prefs.pull<String>("startLocLong")
                val ld2=MyApp.prefs.pull<String>("endLocLong")

                binding.latTextViewStart.text= "Starting Latitude:\n$lt1"
                binding.lngTextViewStart.text= "Starting Longitude\n$ld1"
                binding.latTextViewEnd.text= "Ending Latitude:\n$lt2"
                binding.lngTextViewEnd.text= "Ending Longitude\n$ld2"


                val totalDistance=distance(lat1 = lt1.fullTrim().toDouble(), lat2 = lt2.fullTrim().toDouble(), lon1 = ld1.fullTrim().toDouble(), lon2 = ld2.fullTrim().toDouble())
                binding.tvTotalDistance.text= "Total Distance\n${totalDistance}"
            }
            else{
                Toast.makeText(this, "Not Found! (Start First)", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun hideViews(){
        binding.btnShow.visibility=View.GONE
        binding.linearLayoutStart.visibility=View.GONE
        binding.linearLayoutEnd.visibility=View.GONE
        binding.view1.visibility=View.GONE
        binding.view1.visibility=View.GONE
        binding.tvTotalDistance.visibility=View.GONE
    }

    private fun showViews(){
        binding.linearLayoutStart.visibility=View.VISIBLE
        binding.linearLayoutEnd.visibility=View.VISIBLE
        binding.view1.visibility=View.VISIBLE
        binding.view1.visibility=View.VISIBLE
        binding.tvTotalDistance.visibility=View.VISIBLE

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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
            ) {
                locationPermissions.launch(
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,  android.Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            }else{
                startService(service)
                binding.btnStop.visibility=View.VISIBLE
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
    /*    val poi=POI(0,locationEvent.longitude.toString(),locationEvent.latitude.toString())
        locationViewModel.addLocation(poi)*/
    }


    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist = (sin(deg2rad(lat1))
                * sin(deg2rad(lat2))
                + (cos(deg2rad(lat1))
                * cos(deg2rad(lat2))
                * cos(deg2rad(theta))))
        dist = acos(dist)
        dist = rad2deg(dist)
        dist *= 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }

    fun String.fullTrim() = trim().replace("\uFEFF", "")

    private fun isMyServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    companion object{
        var isFromStop=false
    }
}

