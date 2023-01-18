package com.andreykaranik.journeys

import android.app.Application
import androidx.fragment.app.Fragment
import com.andreykaranik.journeys.models.PointService
import com.andreykaranik.journeys.models.TripService
import com.andreykaranik.journeys.models.User
import com.andreykaranik.journeys.models.UserService

class App : Application() {
    lateinit var tripService : TripService
    lateinit var userService: UserService
    val pointService = PointService()

    override fun onCreate() {
        super.onCreate()
        tripService = TripService(applicationContext)
        userService = UserService(applicationContext)
    }
}

fun Fragment.navigator() = requireActivity() as Navigator