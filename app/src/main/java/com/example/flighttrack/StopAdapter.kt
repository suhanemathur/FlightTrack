//package com.example.flighttrack
//
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.TextView
//import androidx.recyclerview.widget.RecyclerView
//import com.example.flighttrack.R
//
//class StopAdapter(private var stops: List<Stop>, private var useMiles: Boolean) :
//    RecyclerView.Adapter<StopAdapter.ViewHolder>() {
//
//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val stopName: TextView = view.findViewById(R.id.tvStopName)
//        val distance: TextView = view.findViewById(R.id.tvDistance)
//        val visaRequirement: TextView = view.findViewById(R.id.tvVisaRequirement)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.item_stop, parent, false)
//        return ViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val stop = stops[position]
//        val distanceValue = if (useMiles) Utils.kmToMiles(stop.distanceKm) else stop.distanceKm
//        val unit = if (useMiles) "miles" else "km"
//
//        holder.stopName.text = stop.name
//        holder.distance.text = holder.itemView.context.getString(R.string.distance_format, distanceValue.toDouble(), unit)
//        holder.visaRequirement.text = holder.itemView.context.getString(R.string.visa_format, stop.visaRequirement)
//    }
//
//    override fun getItemCount(): Int = stops.size
//
//    fun updateUnit(useMiles: Boolean) {
//        this.useMiles = useMiles
//        notifyItemRangeChanged(0, stops.size) // Notify all items that the unit has changed
//    }
//}

package com.example.flighttrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class StopAdapter(
    private var stops: List<Stop>,
    private var useMiles: Boolean,
    private var currentStopIndex: Int // Track current stop
) : RecyclerView.Adapter<StopAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val stopName: TextView = view.findViewById(R.id.tvStopName)
        val distance: TextView = view.findViewById(R.id.tvDistance)
        val visaRequirement: TextView = view.findViewById(R.id.tvVisaRequirement)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stop, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val stop = stops[position]
        val distanceValue = if (useMiles) Utils.kmToMiles(stop.distanceKm) else stop.distanceKm
        val unit = if (useMiles) "miles" else "km"

        holder.stopName.text = stop.name
        holder.distance.text = holder.itemView.context.getString(R.string.distance_format, distanceValue.toDouble(), unit)
        holder.visaRequirement.text = holder.itemView.context.getString(R.string.visa_format, stop.visaRequirement)

        // Highlight current stop
        if (position == currentStopIndex) {
            holder.stopName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_dark)) // Change color to blue
            holder.stopName.text = "▶ ${stop.name}" // Add indicator to the stop name
        } else {
            holder.stopName.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black)) // Default color
        }
    }

    override fun getItemCount(): Int = stops.size

    fun updateUnit(useMiles: Boolean) {
        this.useMiles = useMiles
        notifyDataSetChanged() // Refresh list when units change
    }

    fun updateCurrentStop(newIndex: Int) {
        currentStopIndex = newIndex
        notifyDataSetChanged() // Refresh list to highlight new current stop
    }
}
