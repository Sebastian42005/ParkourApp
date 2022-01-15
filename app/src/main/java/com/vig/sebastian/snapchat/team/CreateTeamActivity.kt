package com.vig.sebastian.snapchat.team

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class CreateTeamActivity : AppCompatActivity() {
    var key = ""
    var teamName = ""
    val addUserToTeamList = ArrayList<AddUserToTeamClass>()
    lateinit var teamPicImageView: ImageView
    lateinit var imageUri: Uri
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_team)
        val teamNameEditText: EditText = findViewById(R.id.teamNameEditText)
        val teamPasswordEditText: EditText = findViewById(R.id.teamPasswordEditText)
        val createTeamBtn: Button = findViewById(R.id.createTeamBtn)
        val addUsersToTeamBtn : ImageView = findViewById(R.id.addUsersToTeamBtn)
        val addUsersListView: ListView = findViewById(R.id.addUserToTeamListView)
        val addUserToTeamLayout: RelativeLayout = findViewById(R.id.addUserToTeamLayout)
        teamPicImageView = findViewById(R.id.teamPicImageView)
        val backBtn : ImageView = findViewById(R.id.backBtn)

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        teamPicImageView.setOnClickListener {
            choosePicture()
        }

        createTeamBtn.setOnClickListener {
            val teamName = teamNameEditText.text.toString()
            val teamPassword = teamPasswordEditText.text.toString()
            Database.createTeam(teamName, teamPassword) {success, key ->
                if (success) {
                    this.teamName = teamName
                    this.key = key
                    Database.getFriendsList(Global.username) {
                        if (it.size != 0) addUserToTeamLayout.visibility = View.VISIBLE else{
                            uploadPicture(key)
                            super.onBackPressed()
                        }
                    }
                }else {
                    if (key == "password") {
                        YoYo.with(Techniques.Shake).duration(300).playOn(teamPasswordEditText)
                        teamPasswordEditText.error = getString(R.string.error_empty)
                        teamPasswordEditText.requestFocus()
                    }else {
                        YoYo.with(Techniques.Shake).duration(300).playOn(teamNameEditText)
                        teamNameEditText.error = getString(R.string.error_empty)
                        teamNameEditText.requestFocus()
                    }
                }
            }
        }
        Database.getFriendsList(Global.username) {
            for (user in it) {
                addUserToTeamList.add(AddUserToTeamClass(user, false))
            }
            val adapter = AddUserToTeamAdapter(this, R.layout.add_user_to_team_layout, addUserToTeamList)
            addUsersListView.adapter = adapter
        }

        addUsersToTeamBtn.setOnClickListener {
            val list = ArrayList<String>()
            for (user in addUserToTeamList) {
                if (user.isChecked) list.add(user.username)
            }
            Toast.makeText(this, getString(R.string.team_created), Toast.LENGTH_SHORT).show()
            Database.addUsersToTeam(list, key, teamName)
            super.onBackPressed()
        }
    }

    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            teamPicImageView.setImageURI(data.data!!)
        }
    }
    private fun uploadPicture(teamKey: String) {
        Database.storageReference.child(teamKey).putFile(imageUri).addOnSuccessListener {
            ImageUriListsObject.setTeamPicImageUriHashMap(teamKey, imageUri)
        }
    }
}