package com.andreykaranik.journeys.views

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.andreykaranik.journeys.R
import com.andreykaranik.journeys.tasks.EmptyResult
import com.andreykaranik.journeys.tasks.ErrorResult
import com.andreykaranik.journeys.tasks.PendingResult
import com.andreykaranik.journeys.tasks.SuccessResult
import com.andreykaranik.journeys.viewmodels.AuthorizationViewModel
import com.andreykaranik.journeys.viewmodels.TripListViewModel
import com.andreykaranik.journeys.viewmodels.factory

class AuthenticationActivity : AppCompatActivity() {

    private val viewModel: AuthorizationViewModel by viewModels { factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onStart() {
        super.onStart()
        supportActionBar?.setTitle(R.string.authorization)
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener {
            hideComponents()
            viewModel.login()
        }
        val registerButton = findViewById<Button>(R.id.register_button)
        registerButton.setOnClickListener {
            val intent = Intent(this@AuthenticationActivity, RegistrationActivity::class.java)
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

        viewModel.actionSuccessAuth.observe(this, Observer {
            if (it.getValue() == false) {
                showComponents()
                Toast.makeText(this, R.string.bad_user, Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this@AuthenticationActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
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
