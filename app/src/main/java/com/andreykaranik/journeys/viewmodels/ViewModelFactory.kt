package com.andreykaranik.journeys.viewmodels

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.andreykaranik.journeys.App
import com.andreykaranik.journeys.Navigator

class ViewModelFactory(private val app: App) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when(modelClass) {
            TripListViewModel::class.java -> {
                TripListViewModel(app.tripService)
            }
            TripDetailsViewModel::class.java -> {
                TripDetailsViewModel(app.tripService)
            }
            PointListViewModel::class.java -> {
                PointListViewModel(app.pointService, app.tripService)
            }
            AuthorizationViewModel::class.java -> {
                AuthorizationViewModel(app.userService)
            }
            RegistrationViewModel::class.java -> {
                RegistrationViewModel(app.userService)
            }
            else -> {
                throw IllegalStateException("Unknown view model class")
            }
        }
        return viewModel as T
    }
}

fun Fragment.factory() = ViewModelFactory(requireContext().applicationContext as App)
fun Activity.factory() = ViewModelFactory(applicationContext as App)


fun Fragment.navigator() = requireActivity() as Navigator