package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.test.database.Database
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.vig.sebastian.snapchat.login.LoginActivity

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navView.setupWithNavController(navController)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (sharedPreferences.getString("username", "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        }else login()
    }
    @SuppressLint("CommitPrefEdits")
    private fun login() {
        val username = sharedPreferences.getString("username", "").toString()
        val password = sharedPreferences.getString("password", "").toString()
        if (!Global.checkIfStringsAreEmpty(username, password)) {
            Database.login(this, username, password) {
                if (it == null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }else {
                    Database.getUserInfo(username) { user ->
                        Global.username = user.username
                        Global.password = user.password
                        Global.description = user.description
                        Global.country = user.country
                        Global.city = user.city
                        Global.age = user.age
                    }
                }
            }
        }
    }
}