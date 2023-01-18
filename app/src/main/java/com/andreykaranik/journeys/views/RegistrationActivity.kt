package com.andreykaranik.journeys.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.models.Itinerary
import com.andreykaranik.journeys.viewmodels.AuthorizationViewModel
import com.andreykaranik.journeys.viewmodels.RegistrationViewModel
import com.andreykaranik.journeys.viewmodels.factory

class RegistrationActivity : AppCompatActivity() {

    private val viewModel: RegistrationViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setTitle(R.string.registration)
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            hideComponents()
            viewModel.register()
        }
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            val intent = Intent(this@RegistrationActivity, AuthenticationActivity::class.java)
            startActivity(intent)
            finish()
        }

        val emailEditText = findViewById<EditText>(R.id.email_edit_text)
        emailEditText.addTextChangedListener {
            viewModel.setEmail(emailEditText.text.toString())
        }
        val passwordEditText = findViewById<EditText>(R.id.password_edit_text)
        passwordEditText.addTextChangedListener {
            viewModel.setPassword(passwordEditText.text.toString())
        }

        viewModel.actionSuccessRegistration.observe(this, Observer {
            if (it.getValue() == true) {
                val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                showComponents()
                Toast.makeText(this, R.string.such_user_already_exists_exception, Toast.LENGTH_SHORT).show()
            }
        })

    }

    private fun showComponents() {
        findViewById<EditText>(R.id.email_edit_text).isEnabled = true
        findViewById<EditText>(R.id.password_edit_text).isEnabled = true
        findViewById<Button>(R.id.register_button).isEnabled = true
        findViewById<Button>(R.id.login_button).isEnabled = true
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.GONE
    }

    private fun hideComponents() {
        findViewById<EditText>(R.id.email_edit_text).isEnabled = false
        findViewById<EditText>(R.id.password_edit_text).isEnabled = false
        findViewById<Button>(R.id.register_button).isEnabled = false
        findViewById<Button>(R.id.login_button).isEnabled = false
        findViewById<ProgressBar>(R.id.progress_bar).visibility = View.VISIBLE
    }
}