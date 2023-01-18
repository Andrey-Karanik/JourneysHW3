package com.andreykaranik.journeys.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.models.TripService
import com.andreykaranik.journeys.models.TripDetails
import com.andreykaranik.journeys.tasks.EmptyResult
import com.andreykaranik.journeys.tasks.PendingResult
import com.andreykaranik.journeys.tasks.Result
import com.andreykaranik.journeys.tasks.SuccessResult

class TripDetailsViewModel(
    private val tripService: TripService
) : BaseViewModel() {

    private val _state = MutableLiveData<State>()
    val state: LiveData<State> = _state

    private val _actionShowToast = MutableLiveData<Event<Int>>()
    val actionShowToast: LiveData<Event<Int>> = _actionShowToast

    private val _actionGoBack = MutableLiveData<Event<Unit>>()
    val actionGoBack: LiveData<Event<Unit>> = _actionGoBack

    private val currentState: State get() = state.value!!

    init {
        _state.value = State(
            tripDetailsResult = EmptyResult(),
            deletingInProgress = false
        )
    }

    fun loadTrip(tripId: Int) {

        if (currentState.tripDetailsResult is SuccessResult) return

        _state.value = currentState.copy(tripDetailsResult = PendingResult())

        tripService.getTripById(tripId).onSuccess { _state.value = currentState.copy(tripDetailsResult = SuccessResult(it)) }
            .onError {
                _actionShowToast.value = Event(R.string.error)
                _actionGoBack.value = Event(Unit)
            }
            .autoCancel()
    }

    data class State(
        val tripDetailsResult: Result<TripDetails>,
        private val deletingInProgress: Boolean
    )

}