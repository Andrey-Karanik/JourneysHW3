package com.andreykaranik.journeys.models

import android.content.Context
import android.os.Environment
import android.util.JsonReader
import android.util.Log
import com.andreykaranik.journeys.NotAllFieldsFilledException
import com.andreykaranik.journeys.TripNotFoundException
import com.andreykaranik.journeys.tasks.SimpleTask
import com.andreykaranik.journeys.tasks.Task
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.util.*
import java.util.concurrent.Callable

typealias TripListener = (trips: List<Trip>) -> Unit

class TripService(private val context: Context) {
    private var trips = mutableListOf<Trip>()
    private var loaded = false

    private var listeners = mutableSetOf<TripListener>()

    fun loadTrips(): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        loadTripsFromJson()
        loaded = true
        notifyChanges()
    })

    fun saveTrips(): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        saveTripsToJson()
    })

    private fun loadTripsFromJson() {
        trips.clear()
        try {
            val input = InputStreamReader(context.openFileInput("my_trips1.json"))
            val jsonString = input.readText()

            val tokener = JSONTokener(jsonString)
            val jsonObject = tokener.nextValue() as JSONObject

            val jsonTrips = jsonObject.getJSONArray("trips")
            for (i in 0 until jsonTrips.length()) {
                val jsonTrip = jsonTrips.getJSONObject(i)
                val id = jsonTrip.getInt("id")
                val image = jsonTrip.getString("image")
                val itinerary = getItineraryFromJson(jsonTrip.getJSONObject("itinerary"))
                trips.add(Trip(id, image, itinerary))
            }
        } catch (e: IOException) {
            saveTrips()
        }
    }

    private fun getItineraryFromJson(jsonItinerary: JSONObject) : Itinerary {
        val startPoint = getPointFromJson(jsonItinerary.getJSONObject("startPoint"))
        val endPoints = getPointListFromJson(jsonItinerary.getJSONArray("endPoints"))
        return Itinerary(startPoint, endPoints)
    }

    private fun getPointListFromJson(jsonPoints: JSONArray) : List<Itinerary.Point> {
        val points = mutableListOf<Itinerary.Point>()
        for (i in 0 until jsonPoints.length()) {
            val point = getPointFromJson(jsonPoints.getJSONObject(i))
            points.add(point)
        }

        return points
    }

    private fun getPointFromJson(jsonPoint: JSONObject) : Itinerary.Point {
        val name = jsonPoint.getString("name")
        val time = jsonPoint.getString("time")
        return Itinerary.Point(name, time)
    }

    private fun saveTripsToJson() {
        val jsonString = convertTripsToJson()
        val output = OutputStreamWriter(context.openFileOutput("my_trips1.json", Context.MODE_PRIVATE))
        output.write(jsonString)
        output.close()
    }

    private fun convertTripsToJson() : String {
        val json = JSONObject()
        val jsonTrips = JSONArray()
        trips.forEach {
            jsonTrips.put(getJsonTrip(it))
        }
        json.put("trips", jsonTrips)

        return json.toString()
    }

    private fun getJsonTrip(trip: Trip) : JSONObject {
        val jsonTrip = JSONObject()
        jsonTrip.put("id", trip.id)
        jsonTrip.put("image", trip.image)
        val jsonItinerary = JSONObject()
        jsonItinerary.put("startPoint", getJsonPoint(trip.itinerary.startPoint))
        jsonItinerary.put("endPoints", getJsonPointArray(trip.itinerary.endPoints))
        jsonTrip.put("itinerary", jsonItinerary)
        return jsonTrip
    }

    private fun getJsonPoint(point: Itinerary.Point) : JSONObject {
        val jsonPoint = JSONObject()
        jsonPoint.put("name", point.name)
        jsonPoint.put("time", point.time)
        return jsonPoint
    }

    private fun getJsonPointArray(points: List<Itinerary.Point>) : JSONArray {
        val jsonPointArray = JSONArray()
        points.forEach {
            jsonPointArray.put(getJsonPoint(it))
        }
        return jsonPointArray
    }


    fun addTrip(trip: Trip) : Task<Unit> = SimpleTask<Unit>(Callable {
        if (trip.itinerary.startPoint.name == "" || trip.itinerary.startPoint.time == "") {
            throw NotAllFieldsFilledException()
        }
        trip.itinerary.endPoints.forEach {
            if (it.name == "" || it.time == "") {
                throw TripNotFoundException()
            }
        }
        trips.add(trip)
        notifyChanges()
    })

    fun getTripById(id: Int) : Task<TripDetails> = SimpleTask<TripDetails>(Callable {
        Thread.sleep(2000)
        val trip = trips.firstOrNull { it.id == id } ?: throw TripNotFoundException()
        return@Callable TripDetails(
            trip = trip,
            details = "description"
        )
    })

    fun deleteTrip(trip: Trip) : Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        val indexToDelete = trips.indexOfFirst { it.id == trip.id }
        if (indexToDelete != -1) {
            trips.removeAt(indexToDelete)
            notifyChanges()
        }
    })

    fun moveTrip(trip: Trip, moveBy: Int) : Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        val oldIndex = trips.indexOfFirst { it.id == trip.id }
        if (oldIndex == -1) {
            return@Callable
        }
        val newIndex = oldIndex + moveBy
        if (newIndex < 0 || newIndex >= trips.size) {
            return@Callable
        }
        Collections.swap(trips, oldIndex, newIndex)
        notifyChanges()
    })

    fun getTripListSize() : Int {
        return trips.size
    }

    fun addListener(listener: TripListener) {
        listeners.add(listener)
        if (loaded) {
            listener(trips)
        }
    }

    fun removeListener(listener: TripListener) {
        listeners.remove(listener)
        if (loaded) {
            listener(trips)
        }
    }

    private fun notifyChanges() {
        if (!loaded) {
            return
        }
        listeners.forEach { it(trips) }
    }

    fun getRandomImage() : String {
        return IMAGES[(0 until IMAGES.size).random()]
    }

    fun getFreeId() : Int {
        var id = 0
        while(true) {
            trips.forEach {
                if (it.id == id) {
                    id++
                    return@forEach
                }
            }
            return id
        }
    }

    companion object {
        private val IMAGES = mutableListOf(
            "https://images.unsplash.com/photo-1589182373726-e4f658ab50f0?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=387&q=80",
            "https://images.unsplash.com/photo-1589802829985-817e51171b92?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80",
            "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80",
            "https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1170&q=80",
            "https://images.unsplash.com/photo-1434394354979-a235cd36269d?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1151&q=80"
        )
    }
}