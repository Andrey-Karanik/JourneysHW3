package com.andreykaranik.journeys.models

import android.content.Context
import android.os.Environment
import android.util.JsonReader
import android.util.Log
import com.andreykaranik.journeys.NotAllFieldsFilledException
import com.andreykaranik.journeys.SuchUserAlreadyExistsException
import com.andreykaranik.journeys.TripNotFoundException
import com.andreykaranik.journeys.UserNotFoundException
import com.andreykaranik.journeys.tasks.SimpleTask
import com.andreykaranik.journeys.tasks.Task
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.io.*
import java.lang.Exception
import java.util.*
import java.util.concurrent.Callable

typealias UserListener = (users: List<User>) -> Unit

class UserService(private val context: Context) {
    private var users = mutableListOf<User>()
    private var loaded = false

    private var listeners = mutableSetOf<UserListener>()

    fun loadUsers(): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        loadUsersFromJson()
        loaded = true
        notifyChanges()
    })

    fun register(email: String, password: String): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        users.forEach {
            if (it.email == email) {
                throw SuchUserAlreadyExistsException()
            }
        }
        saveUserToJson(User(getFreeId(), email, password))
    })

    fun addSuperUser(): Task<Unit> = SimpleTask<Unit>(Callable {
        users.forEach {
            if (it.email == "super") {
                throw Exception()
            }
        }
        saveUserToJson(User(getFreeId(), "super", "super"))
    })

    fun login(email: String, password: String): Task<Unit> = SimpleTask<Unit>(Callable {
        Thread.sleep(2000)
        if (email == "" && password == "") {
            throw UserNotFoundException()
        }
        var exist = false
        users.forEach {
            if (it.email == email && it.password == password) {
                exist = true
                return@forEach
            }
        }
        if (!exist) {
            throw UserNotFoundException()
        }
    })

    private fun loadUsersFromJson() {
        users.clear()
        val input = InputStreamReader(context.openFileInput("users.json"))
        val jsonString = input.readText()

        val tokener = JSONTokener(jsonString)
        val jsonObject = tokener.nextValue() as JSONObject

        val jsonUsers = jsonObject.getJSONArray("users")
        for (i in 0 until jsonUsers.length()) {
            val jsonUser = jsonUsers.getJSONObject(i)
            val id = jsonUser.getInt("id")
            val email = jsonUser.getString("email")
            val password = jsonUser.getString("password")
            users.add(User(id, email, password))
        }
    }

    private fun saveUserToJson(user: User) {
        users.add(user)
        val jsonString = convertUsersToJson()
        val output = OutputStreamWriter(context.openFileOutput("users.json", Context.MODE_PRIVATE))
        output.write(jsonString)
        output.close()
    }

    private fun convertUsersToJson() : String {
        val json = JSONObject()
        val jsonUsers = JSONArray()
        users.forEach {
            jsonUsers.put(getJsonUser(it))
        }
        json.put("users", jsonUsers)

        return json.toString()
    }

    private fun getJsonUser(user: User) : JSONObject {
        val jsonUser = JSONObject()
        jsonUser.put("id", user.id)
        jsonUser.put("email", user.email)
        jsonUser.put("password", user.password)
        return jsonUser
    }

    fun getUserListSize() : Int {
        return users.size
    }

    fun addListener(listener: UserListener) {
        listeners.add(listener)
        if (loaded) {
            listener(users)
        }
    }

    fun removeListener(listener: UserListener) {
        listeners.remove(listener)
        if (loaded) {
            listener(users)
        }
    }

    private fun notifyChanges() {
        if (!loaded) {
            return
        }
        listeners.forEach { it(users) }
    }

    fun getFreeId() : Int {
        var id = 0
        while(true) {
            users.forEach {
                if (it.id == id) {
                    id++
                    return@forEach
                }
            }
            return id
        }
    }
}