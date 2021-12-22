package com.vig.sebastian.snapchat.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.isVisible
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.classes.User
import com.vig.sebastian.snapchat.database.Database

class LoginActivity : AppCompatActivity() {
    val finishedDataList = ArrayList<Boolean>()
    lateinit var registerLayout : ScrollView
    lateinit var loginLayout : RelativeLayout
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        registerLayout = findViewById(R.id.registerRelativeLayout)
        loginLayout = findViewById(R.id.loginRelativeLayout)
        val registerTextView = findViewById<TextView>(R.id.registerTextView)
        val loginEmailEditText = findViewById<EditText>(R.id.loginEmailEditText)
        val loginPasswordEditText = findViewById<EditText>(R.id.loginPasswordEditText)
        val registerUsername = findViewById<EditText>(R.id.registerUsernameEditText)
        val registerPassword = findViewById<EditText>(R.id.registerPasswordEditText)
        val registerCountry = findViewById<EditText>(R.id.registerCountryEditText)
        val registerCity = findViewById<EditText>(R.id.registerCityEditText)
        val registerAge = findViewById<EditText>(R.id.registerAgeEditText)
        val registerBtn = findViewById<Button>(R.id.registerBtn)
        val registerEmail = findViewById<EditText>(R.id.registerEmailEditText)
        val loginBtn = findViewById<Button>(R.id.loginBtn)
        val resetPasswordTextView : TextView = findViewById(R.id.passwordResetTextView)
        val resetPasswordBtn: Button = findViewById(R.id.resetPasswordBtn)
        val resetEmailEditText : EditText = findViewById(R.id.resetEmailEditText)
        val registerBackBtn : ImageView = findViewById(R.id.registerBackBtn)
        val resetPasswordBackBtn: ImageView = findViewById(R.id.resetPasswordBackBtn)
        val resetPasswordLayout  : RelativeLayout = findViewById(R.id.resetPasswordRelativeLayout)

        registerBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutLeft).duration(400).playOn(registerLayout)
            Global.wait(400) {
                registerLayout.visibility = View.GONE
            }
        }

        resetPasswordBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(400).playOn(resetPasswordLayout)
            Global.wait(400) {
                resetPasswordLayout.visibility = View.GONE
            }
        }

        resetPasswordBtn.setOnClickListener {
            Toast.makeText(this, getString(R.string.password_reset_check_mails), Toast.LENGTH_SHORT).show()
            Database.resetPassword(resetEmailEditText.text.toString().trim())
        }

        registerTextView.setOnClickListener {
            registerLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInLeft).duration(400).playOn(registerLayout)
        }

        resetPasswordTextView.setOnClickListener{
            resetPasswordLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInRight).duration(400).playOn(resetPasswordLayout)
        }

        registerBtn.setOnClickListener {
            val username = registerUsername.text.toString().trim()
            val password = registerPassword.text.toString().trim()
            val country = registerCountry.text.toString().trim()
            val city = registerCity.text.toString().trim()
            val email = registerEmail.text.toString().trim()
            if (!Global.checkIfStringsAreEmpty(
                    username,
                    password,
                    country,
                    city,
                    registerAge.text.toString()
                )
            ) {
                val age = registerAge.text.toString().toInt()
                Database.register(User(username, email, "", country, city, age), password) { exception ->
                    if (exception == "") {
                        editor.putString("email", email)
                        editor.putString("password", password)
                        Global.username = username
                        Global.email = email
                        Global.password = password
                        editor.apply()
                        YoYo.with(Techniques.SlideOutLeft).duration(400).playOn(registerLayout)
                        Global.wait(400) {
                            registerLayout.visibility = View.GONE
                        }
                        Toast.makeText(this, "Check your mails to verify!", Toast.LENGTH_SHORT).show()
                    }else {
                        when (exception) {
                            "6" -> {
                                YoYo.with(Techniques.Shake).duration(300).playOn(registerPassword)
                                registerPassword.error = getString(R.string.password_at_least_6_characters)
                                registerPassword.requestFocus()
                            }
                            "email" -> {
                                YoYo.with(Techniques.Shake).duration(300).playOn(registerEmail)
                                registerEmail.error = getString(R.string.email_exists)
                                registerEmail.requestFocus()
                            }
                        }
                    }
                }
            }else {
                Toast.makeText(this, getString(R.string.error_empty), Toast.LENGTH_SHORT).show()
            }
        }
        loginBtn.setOnClickListener {
            val email = loginEmailEditText.text.toString().trim()
            val password = loginPasswordEditText.text.toString().trim()
            if (!Global.checkIfStringsAreEmpty(email, password)) {
                Database.login(editor, this, email, password) {
                    if (it != null) {
                        Global.username = it.username
                        Global.description = it.description
                        Global.country = it.country
                        Global.email = email
                        Global.city = it.city
                        Global.age = it.age
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()
                        getDataFromDatabase()
                    }
                }
            }
        }
    }

    private fun getDataFromDatabase() {
        Database.getUserProfilePic(Global.username) { uri ->
            ImageUriListsObject.setProfilePicImageUriHashMap(Global.username, uri)
            addData()
        }

        Database.getFirst10PostsFromFriends {
            addData()
        }

        Database.getPostsFromUser(Global.username) {postList ->
            postList.sort()
            if (postList.size == 0) addData()
            for (post in postList) {
                Database.getImageUriFromUser(Global.username, post.uploadPostClass.key) {
                    ImageUriListsObject.setPostImageUriHashMap(post.uploadPostClass.key, it)
                    if (post.uploadPostClass.key == postList[postList.size - 1].uploadPostClass.key) addData()
                }
            }
        }

        Database.getFriendProfilePics { userList, uriList ->
            for (position in userList.indices) {
                ImageUriListsObject.setProfilePicImageUriHashMap(userList[position], uriList[position])
            }
            addData()
        }
    }

    private fun addData() {
        finishedDataList.add(true)
        if (finishedDataList.size == 4) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if (registerLayout.isVisible) {
            YoYo.with(Techniques.SlideOutLeft).duration(400).playOn(registerLayout)
            Global.wait(400) {
                registerLayout.visibility = View.GONE
            }
        }
    }
}