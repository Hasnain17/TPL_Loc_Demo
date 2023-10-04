package com.app.tplmaps.tplloctemp.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.tplmaps.tplloctemp.databinding.LayoutLocationBinding
import com.app.tplmaps.tplloctemp.db.model.POI

/**
 * @Author: Muhammad Hasnain Altaf
 * @Date: 04/10/2023
 */
class LocationAdapter(private var poiList: List<POI>
): RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {
    class LocationViewHolder(val binding: LayoutLocationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: POI) {
            binding.tvLatitude.text = data.latitude
            binding.tvLongitude.text = data.longitude
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutLocationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(
        holder: LocationViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.bind(poiList[position])
    }
    override fun getItemCount(): Int {
        return poiList.size
    }

    fun updateData(newFavoriteList: List<POI>) {
        poiList = newFavoriteList
        notifyDataSetChanged()
    }
}