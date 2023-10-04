package com.app.tplmaps.tplloctemp.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.tplmaps.tplloctemp.db.model.POI

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
@Dao
interface DaoLocation {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addLocation(poi: POI)

    @Query("Select * from tb_locations order by id Desc")
    fun getAllResults(): LiveData<List<POI>>


    @Update
    suspend fun updateLocation(vararg items: POI)

    @Delete
    suspend fun deleteLocation(poi: POI)

}