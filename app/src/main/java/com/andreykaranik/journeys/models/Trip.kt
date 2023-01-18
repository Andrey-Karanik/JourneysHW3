package com.andreykaranik.journeys.models


data class Itinerary(
    var startPoint: Point,
    var endPoints: List<Point>
) {
    data class Point(
        var name: String,
        var time: String
    )
}

data class Trip(
    var id: Int,
    var image: String,
    var itinerary: Itinerary
)

data class TripDetails(
    var trip: Trip,
    var details: String
)
