package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.content.ContentResolver
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
import com.vig.sebastian.snapchat.chat.team.ClickedTeamChatObject
import com.vig.sebastian.snapchat.chat.team.TeamChatActivity
import com.vig.sebastian.snapchat.chat.user.ClickedChatObject
import com.vig.sebastian.snapchat.chat.user.UserChatActivity
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
    lateinit var progressBar: ProgressBar
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("CommitPrefEdits", "RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        setContentView(R.layout.activity_loading_screen)
        if (TestObject.canStart == 2) {
            progressBar = findViewById(R.id.progressBar)
            loadingScreenImageView = findViewById(R.id.loadingScreenImageView)
            loadingScreenProgressTextView = findViewById(R.id.loadingScreenProgressTextView)
            sharedPreferences = application.getSharedPreferences("save", Context.MODE_PRIVATE)
            editor = sharedPreferences.edit()

            if (sharedPreferences.getString("email", "") == "") {
                startActivity(Intent(this, LoginActivity::class.java))
            } else login {
                Database.getUserProfilePic(Global.username) { uri ->
                    ImageUriListsObject.setProfilePicImageUriHashMap(Global.username, uri)
                    println("Data: 1")
                    addData()
                }

                Database.getFirst10PostsFromFriends {
                    println("Data: 2")
                    addData()
                }

                Database.getExplorePosts(false, "", "") {
                    addData()
                }

                Database.getPostsFromUser(Global.username) { postList ->
                        postList.sort()
                        if (postList.size == 0) {
                            println("Data: 4")
                            addData()
                        }
                        for (post in postList) {
                            Database.getImageUriFromUser(Global.username, post.uploadPostClass.key) {
                                ImageUriListsObject.setPostImageUriHashMap(
                                    post.uploadPostClass.key,
                                    it
                                )
                                if (post.uploadPostClass.key == postList[postList.size - 1].uploadPostClass.key) {
                                    println("Data: 4")
                                    addData()
                                }
                            }
                        }
                }

                Database.getFriendProfilePics { userList, uriList ->
                    for (position in userList.indices) {
                        ImageUriListsObject.setProfilePicImageUriHashMap(
                            userList[position],
                            uriList[position]
                        )
                    }
                    println("Data: 5")
                    addData()
                }
            }
        }else TestObject.canStart = 2
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
                        Global.getLocation(this, this) {
                            Database.reference.child("User").child(Global.username).child("city").setValue(it.city)
                            Database.reference.child("User").child(Global.username).child("country").setValue(it.country)
                            Global.city = it.city
                            Global.country = it.country
                        }
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
        TestObject.currentProgress ++
        progressBar.progress = TestObject.currentProgress
        if (progressBar.progress != 5) loadingScreenProgressTextView.text = "${progressBar.progress * 25}%"
        when (TestObject.currentProgress) {
            1 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_1)
            2 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_2)
            3 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_3)
            4 -> loadingScreenImageView.foreground = getDrawable(R.drawable.parkour_4)
            5 -> onSharedIntent()
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
                    } else startNormal()
                } else startNormal()
            } else startNormal()
    }
    private fun startNormal() {
        if (intent.getStringExtra("key") != null) {
            ClickedTeamChatObject.teamKey = intent.getStringExtra("key")!!
            ClickedTeamChatObject.teamName = intent.getStringExtra("name")!!
            ClickedTeamChatObject.admin = intent.getStringExtra("admin")!!
            ClickedTeamChatObject.password = intent.getStringExtra("password")!!
            startActivity(Intent(this, TeamChatActivity::class.java))
        }else startActivity(Intent(this, MainActivity::class.java))
    }
}