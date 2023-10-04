package com.app.tplmaps.tplloctemp.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */

@Entity(tableName = "tb_locations")
data class POI(@PrimaryKey(autoGenerate = true)val id:Int, val longitude:String, val latitude:String)
