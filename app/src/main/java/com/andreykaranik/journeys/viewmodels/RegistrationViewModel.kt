package com.andreykaranik.journeys.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripActionListener
import com.andreykaranik.journeys.models.*
import com.andreykaranik.journeys.tasks.*

class RegistrationViewModel(
    private val userService: UserService
) : BaseViewModel() {

    private val _actionSuccessRegistration = MutableLiveData<Event<Boolean>>()
    val actionSuccessRegistration: LiveData<Event<Boolean>> = _actionSuccessRegistration

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    init {

    }

    fun register() {
        userService.loadUsers()
            .onSuccess {
                userService.register(email.value ?: "", password.value ?: "")
                    .onSuccess {
                        _actionSuccessRegistration.value = Event(true)
                    }
                    .onError {
                        _actionSuccessRegistration.value = Event(false)
                    }
                    .autoCancel()
            }
            .onError {
                userService.addSuperUser().onSuccess {
                    register()
                }.autoCancel()
            }
            .autoCancel()
    }

    fun setEmail(text: String) {
        _email.value = text
    }

    fun setPassword(text: String) {
        _password.value = text
    }

}