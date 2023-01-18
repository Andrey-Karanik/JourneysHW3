package com.andreykaranik.journeys.models

typealias PointListener = (points: List<Itinerary.Point>) -> Unit

class PointService {
    private var points = mutableListOf<Itinerary.Point>()
    private var listeners = mutableSetOf<PointListener>()

    fun createPoints() {
        clearPoints()
        addEmptyPoint()
        addEmptyPoint()
    }

    fun addPoint(point: Itinerary.Point) {
        points.add(point)
        notifyChanges()
    }

    fun addEmptyPoint() {
        points.add(Itinerary.Point("", ""))
        notifyChanges()
    }

    fun getPointByIndex(index: Int): Itinerary.Point {
        return points[index]
    }

    fun getAllEndPoints() : List<Itinerary.Point> {
        return points.drop(1)
    }

    fun clearPoints() {
        points.clear()
    }

    fun addListener(listener: PointListener) {
        listeners.add(listener)
        listener(points)
    }

    fun removeListener(listener: PointListener) {
        listeners.remove(listener)
        listener(points)
    }

    private fun notifyChanges() {
        listeners.forEach { it(points) }
    }
}