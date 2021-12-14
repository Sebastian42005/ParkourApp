package com.vig.sebastian.snapchat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import androidx.appcompat.app.AppCompatDelegate
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.login.LoginActivity

class LoadingScreenActivity : AppCompatActivity() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    val finishedDataList = ArrayList<Boolean>()
    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_screen)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

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
    private fun addData() {
        finishedDataList.add(true)
        if (finishedDataList.size == 3) {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}