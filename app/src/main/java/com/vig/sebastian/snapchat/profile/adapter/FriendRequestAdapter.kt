package com.vig.sebastian.snapchat.profile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R

class FriendRequestAdapter(context: Context, private val int: Int, arrayList : ArrayList<String>) : ArrayAdapter<String>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val username = getItem(position)!!
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)

        val acceptFriendRequestBtn = view.findViewById<ImageView>(R.id.acceptFriendRequestBtn)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)

        usernameTextView.text = username

        Database.getUserProfilePic(username) {
            Glide.with(context).load(it).into(profilePicImageView)
        }

        acceptFriendRequestBtn.setOnClickListener {
            Database.acceptFriendRequest(username)
        }

        return view
    }
}