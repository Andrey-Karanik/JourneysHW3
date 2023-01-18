package com.andreykaranik.journeys.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripActionListener
import com.andreykaranik.journeys.models.*
import com.andreykaranik.journeys.tasks.*

class PointListViewModel(
    private val pointService: PointService,
    private val tripService: TripService
) : BaseViewModel() {

    private val _points = MutableLiveData<List<Itinerary.Point>>()
    val points: LiveData<List<Itinerary.Point>> = _points

    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionBadSave = MutableLiveData<Event<Unit>>()
    val actionBadSave: LiveData<Event<Unit>> = _actionBadSave

    private val listener: PointListener = {
        _points.value = it
    }

    init {
        pointService.addListener(listener)
        createPoints()
    }

    override fun onCleared() {
        super.onCleared()
        pointService.removeListener(listener)
    }

    fun createPoints() {
        pointService.createPoints()
    }

    fun addPoint() {
        pointService.addEmptyPoint()
    }

    fun addTrip() {
        val itinerary = Itinerary(
            Itinerary.Point(pointService.getPointByIndex(0).name, pointService.getPointByIndex(0).time), getAllEndPoints())
        val trip = Trip(tripService.getFreeId(), tripService.getRandomImage(), itinerary)
        tripService.addTrip(trip).onSuccess {
            tripService.saveTrips().onSuccess {
                _actionGoBack.value = Event(Unit)
            }.onError {
                _actionShowToast.value = Event(R.string.error)
                _actionBadSave.value = Event(Unit)
            }.autoCancel()
        }.onError {
            _actionShowToast.value = Event(R.string.not_all_fields_filled_exception)
            _actionBadSave.value = Event(Unit)
        }.autoCancel()
    }

    fun getPoint(index: Int) : Itinerary.Point {
        return pointService.getPointByIndex(index)
    }

    fun getAllEndPoints() : List<Itinerary.Point> {
        return pointService.getAllEndPoints()
    }

}