package com.andreykaranik.journeys.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.TripActionListener
import com.andreykaranik.journeys.models.*
import com.andreykaranik.journeys.tasks.*

class AuthorizationViewModel(
    private val userService: UserService
) : BaseViewModel() {

    private val _actionSuccessAuth = MutableLiveData<Event<Boolean>>()
    val actionSuccessAuth: LiveData<Event<Boolean>> = _actionSuccessAuth

    private val _email = MutableLiveData<String>()
    val email: LiveData<String> = _email

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    fun login() {
        userService.loadUsers()
            .onSuccess {
                userService.login(email.value ?: "", password.value ?: "")
                    .onSuccess {
                        _actionSuccessAuth.value = Event(true)
                    }
                    .onError {
                        _actionSuccessAuth.value = Event(false)
                    }
                    .autoCancel()
            }
            .onError {
                userService.addSuperUser().onSuccess {
                    login()
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