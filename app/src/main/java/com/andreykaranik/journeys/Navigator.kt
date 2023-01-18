package com.andreykaranik.journeys

import com.andreykaranik.journeys.models.Trip

interface Navigator {
    fun showAddTrip()
    fun showDetails(trip: Trip)
    fun goBack()
    fun toast(messageRes: Int)
}