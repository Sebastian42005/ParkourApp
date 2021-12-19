package com.vig.sebastian.snapchat.chat.user

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.MessageAdapter
import com.vig.sebastian.snapchat.classes.MessageClass
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedProfileObject

class UserChatActivity : AppCompatActivity() {
    lateinit var chatListView : ListView
    val chatList = ArrayList<MessageClass>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_chat)
        val username = ClickedChatObject.username
        val profilePicImageView: ImageView = findViewById(R.id.profilePicImageView)
        val usernameTextView: TextView = findViewById(R.id.profileUsernameTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        chatListView = findViewById(R.id.chatListView)
        val sendMessageBtn: ImageView = findViewById(R.id.sendMessageBtn)
        val messageEditText: EditText = findViewById(R.id.messageEditText)

        if (ImageUriListsObject.profilePicsList.contains(username)) {
            if (ImageUriListsObject.getProfilePic(username) != null) {
                Glide.with(this).load(ImageUriListsObject.getProfilePic(username))
                    .into(profilePicImageView)
            }
        }else {
            Database.getUserProfilePic(username) {
                ImageUriListsObject.setProfilePicImageUriHashMap(username, it)
                Glide.with(this).load(it).into(profilePicImageView)
            }
        }
        usernameTextView.text = username

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        chatListView.setOnItemLongClickListener { parent, view, position, id ->
            if (chatList[position].username == Global.username) {
                AlertDialog.Builder(this).setMessage(getString(R.string.delete_message)).setPositiveButton(getString(R.string.delete)) {_,_->
                    Database.deleteUserMessage(username, chatList[position].key)
                }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
            }
            return@setOnItemLongClickListener false
        }

        sendMessageBtn.setOnClickListener {
            Database.sendMessageToUser(ClickedChatObject.username, messageEditText.text.toString())
            messageEditText.setText("")
        }
        Database.getMessagesFromUser(ClickedChatObject.username) { messagesList ->
            for (message in messagesList) {
                chatList.add(message)
            }
            val adapter = MessageAdapter(this, R.layout.message_layout, messagesList)
            chatListView.adapter = adapter
        }
    }
}