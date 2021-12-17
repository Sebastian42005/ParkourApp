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
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.login.LoginActivity
import com.vig.sebastian.snapchat.profile.PostObject
import com.vig.sebastian.snapchat.profile.PostObjectType
import com.vig.sebastian.snapchat.profile.UploadPostActivity
import java.io.FileNotFoundException

class LoadingScreenActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    var currentProgress = 0
    lateinit var progressBar: ProgressBar
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)
        progressBar = findViewById(R.id.progressBar)
        sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()

        if (sharedPreferences.getString("username", "") == "") {
            startActivity(Intent(this, LoginActivity::class.java))
        }else login {
            Database.getUserProfilePic(Global.username) { uri ->
                uri?.let {
                    ImageUriListsObject.setProfilePicImageUriHashMap(Global.username, uri)
                }
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
                    uriList[position]?.let {
                        ImageUriListsObject.setProfilePicImageUriHashMap(userList[position], it)
                    }
                }
                addData()
            }
        }
    }
    @SuppressLint("CommitPrefEdits")
    private fun login(unit: () -> Unit) {
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
                        unit()
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun addData() {
        progressBar.max = 4
        progressBar.min = 0
        currentProgress ++
        progressBar.progress = currentProgress
        if (currentProgress == 4) {
            onSharedIntent()
        }
    }
    private fun onSharedIntent() {
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
                }else startActivity(Intent(this, MainActivity::class.java))
            }else startActivity(Intent(this, MainActivity::class.java))
        }else startActivity(Intent(this, MainActivity::class.java))
    }
}