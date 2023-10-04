package com.app.tplmaps.tplloctemp.db.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.app.tplmaps.tplloctemp.db.repo.LocationRepository

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
class LocationVmProviderFactory(val app: Application, private val locationRepository: LocationRepository):
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LocationViewModel(app = app, locationRepository =locationRepository ) as T
    }
}