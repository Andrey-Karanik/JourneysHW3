package com.andreykaranik.journeys.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreykaranik.journeys.PointAdapter
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripAdapter
import com.andreykaranik.journeys.models.Itinerary
import com.andreykaranik.journeys.models.Trip
import com.andreykaranik.journeys.models.TripService
import com.andreykaranik.journeys.tasks.SuccessResult
import com.andreykaranik.journeys.viewmodels.PointListViewModel
import com.andreykaranik.journeys.viewmodels.TripDetailsViewModel
import com.andreykaranik.journeys.viewmodels.factory
import com.andreykaranik.journeys.viewmodels.navigator
import com.bumptech.glide.Glide

class AddTripFragment : Fragment(R.layout.fragment_trip_add) {

    private lateinit var adapter: PointAdapter

    private val viewModel: PointListViewModel by viewModels { factory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = PointAdapter(view.context)

        viewModel.points.observe(viewLifecycleOwner, Observer {
            adapter.points = it
        })

        val layoutManager = LinearLayoutManager(view.context)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val addPointButton = view.findViewById<Button>(R.id.add_point_item_button)
        addPointButton.setOnClickListener {
            viewModel.addPoint()
        }

        val addTripButton = view.findViewById<Button>(R.id.add_trip_button)
        addTripButton.setOnClickListener {
            viewModel.addTrip()
            addPointButton.visibility = View.GONE
            addTripButton.visibility = View.GONE
            recyclerView.visibility = View.GONE
            view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
        }

        viewModel.actionGoBack.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { navigator().goBack() }
        })
        viewModel.actionShowToast.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        })
        viewModel.actionBadSave.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let {
                addPointButton.visibility = View.VISIBLE
                addTripButton.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                view.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
            }
        })

    }

}