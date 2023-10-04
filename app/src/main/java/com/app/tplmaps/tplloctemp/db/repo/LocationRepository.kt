package com.app.tplmaps.tplloctemp.db.repo

import android.location.Location
import com.app.tplmaps.tplloctemp.db.database.LocationsDatabase
import com.app.tplmaps.tplloctemp.db.model.POI

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
class LocationRepository(private val db:LocationsDatabase) {

    suspend fun insertLocation(poi: POI)=db.locationDao().addLocation(poi)
    suspend fun deleteLocation(poi: POI)=db.locationDao().deleteLocation(poi)
    fun getAllLocations()=db.locationDao().getAllResults()

    suspend fun updateStream(poi: POI)=db.locationDao().updateLocation(poi)


}