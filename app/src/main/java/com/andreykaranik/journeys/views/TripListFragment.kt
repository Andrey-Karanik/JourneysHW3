package com.andreykaranik.journeys.views

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripAdapter
import com.andreykaranik.journeys.models.Itinerary
import com.andreykaranik.journeys.models.Trip
import com.andreykaranik.journeys.models.TripService
import com.andreykaranik.journeys.tasks.EmptyResult
import com.andreykaranik.journeys.tasks.ErrorResult
import com.andreykaranik.journeys.tasks.PendingResult
import com.andreykaranik.journeys.tasks.SuccessResult
import com.andreykaranik.journeys.viewmodels.TripListViewModel
import com.andreykaranik.journeys.viewmodels.factory
import com.andreykaranik.journeys.viewmodels.navigator
import com.google.android.material.button.MaterialButton

class TripListFragment : Fragment(R.layout.fragment_trip_list) {

    private lateinit var adapter: TripAdapter

    private val viewModel: TripListViewModel by viewModels { factory() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = TripAdapter(viewModel)

        val noTripsTextView = view.findViewById<View>(R.id.no_trips_container)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        val tryAgainContainer = view.findViewById<LinearLayout>(R.id.try_again_container)
        val progressBar = view.findViewById<ProgressBar>(R.id.progress_bar)
        val editButton = view.findViewById<View>(R.id.edit_trip_button)

        viewModel.trips.observe(viewLifecycleOwner, Observer {
            hideAll()
            when (it) {
                is SuccessResult -> {
                    recyclerView.visibility = View.VISIBLE
                    if (viewModel.editMode.value == false) {
                        editButton.visibility = View.VISIBLE
                    }
                    adapter.trips = it.data
                }
                is ErrorResult -> {
                    tryAgainContainer.visibility = View.VISIBLE
                }
                is PendingResult -> {
                    progressBar.visibility = View.VISIBLE
                }
                is EmptyResult -> {
                    if (viewModel.editMode.value == false) {
                        noTripsTextView.visibility = View.VISIBLE
                    }
                }
            }
        })

        viewModel.actionShowDetails.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { trip -> navigator().showDetails(trip) }
        })
        viewModel.actionShowToast.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let { messageRes -> navigator().toast(messageRes) }
        })
        viewModel.actionSaveTrips.observe(viewLifecycleOwner, Observer {
            it.getValue()?.let {
                viewModel.setEditMode(false)
                progressBar.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        })
        viewModel.editMode.observe(viewLifecycleOwner, Observer {
            adapter.editMode = it
            if (it) {
                showEditComponents()
            } else {
                hideEditComponents()
            }
        })

        val layoutManager = LinearLayoutManager(view.context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter

        val addButton = view.findViewById<MaterialButton>(R.id.add_trip_button)
        addButton.setOnClickListener {
            navigator().showAddTrip()
        }
        val extraAddButton = view.findViewById<Button>(R.id.extra_add_trip_button)
        extraAddButton.setOnClickListener {
            navigator().showAddTrip()
        }
        editButton.setOnClickListener {
            viewModel.setEditMode(true)
        }
        val saveButton = view.findViewById<Button>(R.id.save_trips_button)
        saveButton.setOnClickListener {
            viewModel.saveTrips()
            hideAll()
            progressBar.visibility = View.VISIBLE
            saveButton.visibility = View.GONE
        }
    }

    private fun hideAll() {
        view?.findViewById<View>(R.id.no_trips_container)?.visibility = View.GONE
        view?.findViewById<RecyclerView>(R.id.recycler_view)?.visibility = View.GONE
        view?.findViewById<LinearLayout>(R.id.try_again_container)?.visibility = View.GONE
        view?.findViewById<ProgressBar>(R.id.progress_bar)?.visibility = View.GONE
        view?.findViewById<View>(R.id.edit_trip_button)?.visibility = View.GONE
    }

    private fun showEditComponents() {
        view?.findViewById<View>(R.id.edit_trip_button)?.visibility = View.GONE
        view?.findViewById<View>(R.id.save_trips_button)?.visibility = View.VISIBLE
        view?.findViewById<View>(R.id.add_trip_button)?.visibility = View.GONE
    }

    private fun hideEditComponents() {
        view?.findViewById<View>(R.id.edit_trip_button)?.visibility = View.VISIBLE
        view?.findViewById<View>(R.id.save_trips_button)?.visibility = View.GONE
        view?.findViewById<View>(R.id.add_trip_button)?.visibility = View.VISIBLE
    }


}