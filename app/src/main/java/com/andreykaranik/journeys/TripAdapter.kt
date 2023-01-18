package com.andreykaranik.journeys

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andreykaranik.journeys.models.Trip
import com.andreykaranik.journeys.viewmodels.TripListItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners


interface TripActionListener {
    fun onTripMove(trip: Trip, moveBy: Int)
    fun onTripDelete(trip: Trip)
    fun onTripDetails(trip: Trip)
}

class TripAdapter(private val actionListener: TripActionListener) : RecyclerView.Adapter<TripAdapter.TripViewHolder>(), View.OnClickListener {

    var trips: List<TripListItem> = emptyList()
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }
    var editMode: Boolean = false
        set(newValue) {
            field = newValue
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_trip, parent, false)
        itemView.findViewById<ImageView>(R.id.edit_button).setOnClickListener(this)
        itemView.findViewById<ImageView>(R.id.delete_button).setOnClickListener(this)
        return TripViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val tripListItem = trips[position]
        val trip = tripListItem.trip

        holder.itemView.tag = trip
        holder.editButton.tag = trip
        holder.deleteButton.tag = trip

        if (tripListItem.isInProgress) {
            holder.progressBar.visibility = View.VISIBLE
            holder.editButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
            holder.itemView.setOnClickListener(null)
        } else {
            holder.progressBar.visibility = View.GONE
            holder.editButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE
            holder.itemView.setOnClickListener(this@TripAdapter)
        }

        if (editMode) {
            holder.editContainer.visibility = View.VISIBLE
            holder.itemView.setOnClickListener(null)
        } else {
            holder.editContainer.visibility = View.GONE
        }

        holder.tripNameTextView.text = trip.itinerary.startPoint.time + " - " + trip.itinerary.endPoints.last().time
        holder.tripCompanyTextView.text = trip.itinerary.startPoint.name + " -> " + trip.itinerary.endPoints.last().name

        if (trip.image.isNotBlank()) {
            Glide.with(holder.photoImageView.context)
                .load(trip.image)
                .transform(CenterCrop(), RoundedCorners(48))
                .placeholder(R.drawable.ic_user_avatar)
                .error(R.drawable.ic_user_avatar)
                .into(holder.photoImageView)
        } else {
            holder.photoImageView.setImageResource(R.drawable.ic_user_avatar)
        }
    }

    override fun getItemCount(): Int = trips.size

    class TripViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val photoImageView: ImageView = itemView.findViewById(R.id.photo_image_view)
        val tripNameTextView: TextView = itemView.findViewById(R.id.user_name_text_view)
        val tripCompanyTextView: TextView = itemView.findViewById(R.id.user_company_text_view)
        val editContainer: View = itemView.findViewById(R.id.edit_container)
        val editButton: ImageView = itemView.findViewById(R.id.edit_button)
        val deleteButton: ImageView = itemView.findViewById(R.id.delete_button)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
    }

    override fun onClick(v: View) {
        val trip = v.tag as Trip
        when (v.id) {
            R.id.edit_button -> {

            }
            R.id.delete_button -> {
                actionListener.onTripDelete(trip)
            }
            else -> {
                actionListener.onTripDetails(trip)
            }
        }
    }
}