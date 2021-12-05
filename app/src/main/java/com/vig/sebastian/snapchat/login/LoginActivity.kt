package com.vig.sebastian.snapchat.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.isVisible
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.classes.User

class LoginActivity : AppCompatActivity() {
    lateinit var registerLayout : RelativeLayout
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        registerLayout = findViewById(R.id.registerRelativeLayout)
        val loginLayout = findViewById<RelativeLayout>(R.id.loginRelativeLayout)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        val loginUsername = findViewById<EditText>(R.id.loginUsernameEditText)
        val loginPassword = findViewById<EditText>(R.id.loginPasswordEditText)
        val registerUsername = findViewById<EditText>(R.id.registerUsernameEditText)
        val registerPassword = findViewById<EditText>(R.id.registerPasswordEditText)
        val registerCountry = findViewById<EditText>(R.id.registerCountryEditText)
        val registerCity = findViewById<EditText>(R.id.registerCityEditText)
        val registerAge = findViewById<EditText>(R.id.registerAgeEditText)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val loginBtn = findViewById<Button>(R.id.loginBtn)

        registerTextView.setOnClickListener {
            registerLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInLeft).duration(400).playOn(registerLayout)
        }

        registerBtn.setOnClickListener {
            val username = registerUsername.text.toString().trim()
            val password = registerPassword.text.toString().trim()
            val country = registerCountry.text.toString().trim()
            val city = registerCity.text.toString().trim()
            if (!Global.checkIfStringsAreEmpty(
                    username,
                    password,
                    country,
                    city,
                    registerAge.text.toString()
                )
            ) {
                val age = registerAge.text.toString().toInt()
                Database.register(User(username, password, "", country, city, age)) {
                    startActivity(Intent(this, MainActivity::class.java))
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.apply()
                    startActivity(Intent(this, MainActivity::class.java))
                }
            }
        }
        loginBtn.setOnClickListener {
            val username = loginUsername.text.toString().trim()
            val password = loginPassword.text.toString().trim()
            if (!Global.checkIfStringsAreEmpty(username, password)) {
                Database.login(this, username, password) {
                    if (it != null) {
                        startActivity(Intent(this, MainActivity::class.java))
                        Global.username = it.username
                        Global.password = it.password
                        Global.description = it.description
                        Global.country = it.country
                        Global.city = it.city
                        Global.age = it.age
                        editor.putString("username", username)
                        editor.putString("password", password)
                        editor.apply()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (registerLayout.isVisible) {
            YoYo.with(Techniques.SlideOutRight).duration(400).playOn(registerLayout)
            Global.wait(400) {
                registerLayout.visibility = View.GONE
            }
        }
    }
}