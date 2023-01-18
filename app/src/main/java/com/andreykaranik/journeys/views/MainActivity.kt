package com.andreykaranik.journeys.views

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.andreykaranik.journeys.Navigator
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.models.Trip
import com.google.android.material.navigation.NavigationBarView

class MainActivity : AppCompatActivity(), Navigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, TripListFragment()).commit()
        }
        val bottomNavigationView = findViewById<NavigationBarView>(R.id.nav_view)
        bottomNavigationView.setOnItemSelectedListener {item ->
            when(item.itemId){
                R.id.trips -> {
                    true
                }
                R.id.events -> {
                    true
                }
                R.id.profile -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.fragment_container, fragment)
            .commit()

    }

    override fun showAddTrip() {
        launchFragment(AddTripFragment())
    }

    override fun showDetails(trip: Trip) {
        supportFragmentManager.beginTransaction().addToBackStack(null).replace(R.id.fragment_container, TripDetailsFragment.newInstance(trip.id)).commit()
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun toast(messageRes: Int) {
        Toast.makeText(this, messageRes, Toast.LENGTH_SHORT).show()
    }
}