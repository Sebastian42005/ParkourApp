package com.vig.sebastian.snapchat.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.TestLooperManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.classes.User
import com.vig.sebastian.snapchat.database.Database

class EditProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        val usernameTextView = findViewById<TextView>(R.id.editUsernameTextView)
        val passwordEditText = findViewById<EditText>(R.id.editPasswordEditText)
        val countryEditText = findViewById<EditText>(R.id.editCountryEditText)
        val cityEditText = findViewById<EditText>(R.id.editCityEditText)
        val descriptionEditText = findViewById<EditText>(R.id.editDescriptionEditText)
        val ageEditText = findViewById<EditText>(R.id.editAgeEditText)
        val saveBtn = findViewById<Button>(R.id.saveProfileSettingsBtn)
        val backBtn = findViewById<ImageView>(R.id.backBtn)

        backBtn.setOnClickListener { super.onBackPressed() }

        Database.getUserInfo(Global.username) { user ->
            usernameTextView.text = Global.username
            passwordEditText.setText(user.password)
            countryEditText.setText(user.country)
            cityEditText.setText(user.city)
            descriptionEditText.setText(user.description)
            ageEditText.setText(user.age.toString())

            saveBtn.setOnClickListener {
                if (ageEditText.text.toString().trim() == "") ageEditText.setText("0")
                val username = Global.username
                val password = passwordEditText.text.toString().trim()
                val description = descriptionEditText.text.toString().trim()
                val country = countryEditText.text.toString().trim()
                val city = cityEditText.text.toString().trim()
                val age = ageEditText.text.toString().trim().toInt()
                if (password != "")
                    if (country != "")
                        if (city != "")
                            if (age != 0) {
                                Database.updateProfile(User(
                                        username,
                                        password,
                                        description,
                                        country,
                                        city,
                                        age))
                                super.onBackPressed()
                            }

            }
        }
    }
}