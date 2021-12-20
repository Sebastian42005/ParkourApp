package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.ContactsContract
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import com.google.firebase.database.FirebaseDatabase
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.login.LoginActivity
import com.vig.sebastian.snapchat.profile.PostObject
import com.vig.sebastian.snapchat.profile.PostObjectType
import com.vig.sebastian.snapchat.profile.UploadPostActivity
import java.io.FileNotFoundException

class LoadingScreenActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var loadingScreenImageView: ImageView
    lateinit var loadingScreenProgressTextView: TextView
    var currentProgress = 0
    lateinit var progressBar: ProgressBar
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_loading_screen)
        progressBar = findViewById(R.id.progressBar)
        loadingScreenImageView = findViewById(R.id.loadingScreenImageView)
        loadingScreenProgressTextView = findViewById(R.id.loadingScreenProgressTextView)
        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (sharedPreferences.getString("email", "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        }else login {
            Database.getUserProfilePic(Global.username) { uri ->
                ImageUriListsObject.setProfilePicImageUriHashMap(Global.username, uri)
                addData()
            }

            Database.getFirst10PostsFromFriends {
                addData()
            }

            Database.getExplorePosts(false, "", "") {
                addData()
            }

            Database.getPostsFromUser(Global.username) {postList ->
                println("LISTE: " + postList.size)
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
    }
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits")
    private fun login(unit: () -> Unit) {
        val email = sharedPreferences.getString("email", "").toString()
        val password = sharedPreferences.getString("password", "").toString()
        if (!Global.checkIfStringsAreEmpty(email, password)) {
            Database.login(editor, this, email, password) { user ->
                if (user == null) {
                    startActivity(Intent(this, LoginActivity::class.java))
                }else {
                    Database.getUsername(email) {
                        Global.username = user.username
                        Global.email = user.email
                        Global.description = user.description
                        Global.country = user.country
                        Global.city = user.city
                        Global.age = user.age
                        unit()
                    }
                }
            }
        }
    }
    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData() {
        progressBar.max = 4
        progressBar.min = 0
        currentProgress ++
        progressBar.progress = currentProgress
        if (progressBar.progress != 5) loadingScreenProgressTextView.text = "${progressBar.progress * 25}%"
        when (currentProgress) {
            1 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_1)
            2 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_2)
            3 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_3)
            4 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_4)
            5 -> onSharedIntent()
        }
    }
    private fun onSharedIntent() {
        if (TestObject.canStart) {
            TestObject.canStart = false
            val receiverdIntent = intent
            val receivedAction = receiverdIntent.action
            val receivedType = receiverdIntent.type
            if (receivedAction == Intent.ACTION_SEND) {
                if (receivedType != null) {
                    if (receivedType.startsWith("image/")) {
                        val receiveUri = receiverdIntent
                            .getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as Uri?
                        if (receiveUri != null) {
                            try {
                                PostObject.uri = receiveUri
                                PostObject.type = PostObjectType.INTENT
                                startActivity(Intent(this, UploadPostActivity::class.java))
                            } catch (e: FileNotFoundException) {
                                e.printStackTrace()
                            }
                        }
                    } else startActivity(Intent(this, MainActivity::class.java))
                } else startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
    }
}