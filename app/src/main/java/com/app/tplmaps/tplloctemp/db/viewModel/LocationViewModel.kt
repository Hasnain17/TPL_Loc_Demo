package com.app.tplmaps.tplloctemp.db.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.tplmaps.tplloctemp.db.model.POI
import com.app.tplmaps.tplloctemp.db.repo.LocationRepository
import kotlinx.coroutines.launch

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
class LocationViewModel(app: Application, private val locationRepository: LocationRepository): AndroidViewModel(app) {
    fun addLocation(poi: POI)=viewModelScope.launch {
        locationRepository.insertLocation(poi)
    }

    fun deleteStreams(poi: POI)=viewModelScope.launch {
        locationRepository.deleteLocation(poi)
    }

    fun getAllLocation()=locationRepository.getAllLocations()

    fun updateLocation(poi: POI)=viewModelScope.launch {
        locationRepository.updateStream(poi)
    }
}