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
        val profilePicImageView: ImageView = findViewById(R.id.profilePicImageView)
        val usernameTextView: TextView = findViewById(R.id.profileUsernameTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        chatListView = findViewById(R.id.chatListView)
        val sendMessageBtn: ImageView = findViewById(R.id.sendMessageBtn)
        val messageEditText: EditText = findViewById(R.id.messageEditText)

        Glide.with(this).load(ClickedChatObject.imageUri).into(profilePicImageView)
        usernameTextView.text = ClickedChatObject.username

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        chatListView.setOnItemLongClickListener { parent, view, position, id ->
            AlertDialog.Builder(this).setMessage("Delete message?").setPositiveButton("Delete") {_,_->
                Database.deleteUserMessage(ClickedChatObject.username, chatList[position].key)
            }.setNegativeButton("Cancel") {_,_->}.show()
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