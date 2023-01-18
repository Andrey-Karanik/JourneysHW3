package com.andreykaranik.journeys.tasks

typealias CallBack<T> = (T) -> Unit

interface Task<T> {
    fun onSuccess(callback: CallBack<T>): Task<T>
    fun onError(callback: CallBack<Throwable>): Task<T>
    fun cancel()
    fun await(): T
}