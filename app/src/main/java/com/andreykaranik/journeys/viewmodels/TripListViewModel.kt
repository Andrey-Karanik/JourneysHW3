package com.andreykaranik.journeys.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripActionListener
import com.andreykaranik.journeys.models.TripService
import com.andreykaranik.journeys.models.Trip
import com.andreykaranik.journeys.models.TripListener
import com.andreykaranik.journeys.tasks.*

data class TripListItem(
    val trip: Trip,
    val isInProgress: Boolean
)

class TripListViewModel(
    private val tripService: TripService
) : BaseViewModel(), TripActionListener {

    private val _trips = MutableLiveData<Result<List<TripListItem>>>()
    val trips: LiveData<Result<List<TripListItem>>> = _trips

    private val _actionShowDetails = MutableLiveData<Event<Trip>>()
    val actionShowDetails: LiveData<Event<Trip>> = _actionShowDetails

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionSaveTrips = MutableLiveData<Event<Unit>>()
    val actionSaveTrips: LiveData<Event<Unit>> = _actionSaveTrips

    private val _editMode = MutableLiveData<Boolean>()
    val editMode: LiveData<Boolean> = _editMode

    private val tripIdsInProgress = mutableSetOf<Int>()
    private var tripsResult: Result<List<Trip>> = EmptyResult()
        set(value) {
            field = value
            notifyUpdates()
        }

    private val listener: TripListener = {
        tripsResult = if (it.isEmpty()) {
            EmptyResult()
        } else {
            SuccessResult(it)
        }
    }

    init {
        tripService.addListener(listener)
        loadTrips()
        setEditMode(false)
    }

    override fun onCleared() {
        super.onCleared()
        tripService.removeListener(listener)
    }

    fun loadTrips() {
        tripsResult = PendingResult()
        tripService.loadTrips().onError { tripsResult = ErrorResult(it) }.autoCancel()
    }

    override fun onTripMove(trip: Trip, moveBy: Int) {
        if (isInProgress(trip)) return
        addProgressTo(trip)
        tripService.moveTrip(trip, moveBy)
            .onSuccess {
                removeProgressFrom(trip)
            }
            .onError {
                removeProgressFrom(trip)
                _actionShowToast.value = Event(R.string.error)
            }
            .autoCancel()
    }

    override fun onTripDelete(trip: Trip) {
        if (isInProgress(trip)) return
        addProgressTo(trip)
        tripService.deleteTrip(trip)
            .onSuccess {
                removeProgressFrom(trip)
            }
            .onError {
                removeProgressFrom(trip)
                _actionShowToast.value = Event(R.string.error)
            }
            .autoCancel()
    }

    fun saveTrips() {
        tripService.saveTrips()
            .onSuccess {
                _actionSaveTrips.value = Event(Unit)
            }
            .onError {
                _actionShowToast.value = Event(R.string.error)
            }
            .autoCancel()
    }

    fun setEditMode(mode: Boolean) {
        _editMode.value = mode
    }

    override fun onTripDetails(trip: Trip) {
        _actionShowDetails.value = Event(trip)
    }

    private fun addProgressTo(trip: Trip) {
        tripIdsInProgress.add(trip.id)
        notifyUpdates()
    }

    private fun removeProgressFrom(trip: Trip) {
        tripIdsInProgress.remove(trip.id)
        notifyUpdates()
    }

    private fun isInProgress(trip: Trip) : Boolean {
        return tripIdsInProgress.contains(trip.id)
    }

    private fun notifyUpdates() {
        _trips.postValue(tripsResult.map { trips ->
            trips.map { user -> TripListItem(user, isInProgress(user)) }
        })
    }

}