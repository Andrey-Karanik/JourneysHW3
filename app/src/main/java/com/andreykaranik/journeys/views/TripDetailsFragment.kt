package com.andreykaranik.journeys.views

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.tasks.SuccessResult
import com.andreykaranik.journeys.viewmodels.TripDetailsViewModel
import com.andreykaranik.journeys.viewmodels.factory
import com.andreykaranik.journeys.viewmodels.navigator
import com.bumptech.glide.Glide

class TripDetailsFragment : Fragment(R.layout.fragment_trip_details) {

    private val viewModel: TripDetailsViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadTrip(requireArguments().getInt(ARG_TRIP_ID))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val ARG_TRIP_ID = "ADD_TRIP_ID"

        fun newInstance(tripId: Int): TripDetailsFragment {
            val fragment = TripDetailsFragment()
            fragment.arguments = bundleOf(ARG_TRIP_ID to tripId)
            return fragment
        }
    }

}