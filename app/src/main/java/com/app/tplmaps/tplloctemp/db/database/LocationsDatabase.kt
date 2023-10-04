package com.app.tplmaps.tplloctemp.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.app.tplmaps.tplloctemp.db.dao.DaoLocation
import com.app.tplmaps.tplloctemp.db.model.POI

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */

@Database(entities = [POI::class], version = 1, exportSchema = false)
abstract class LocationsDatabase:RoomDatabase() {
abstract fun locationDao():DaoLocation


    companion object{
        @Volatile
        private var instance: LocationsDatabase?=null
        private val lock=Any()
        operator fun invoke(context: Context)= instance ?: synchronized(lock){
            instance ?: getResultsDbClient(context).also { instance =it }
        }
        private fun getResultsDbClient(context: Context)= Room.databaseBuilder(context.applicationContext,
            LocationsDatabase::class.java,"locations_db").build()
    }
}