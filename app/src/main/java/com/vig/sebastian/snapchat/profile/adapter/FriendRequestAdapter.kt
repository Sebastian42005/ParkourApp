package com.vig.sebastian.snapchat.profile.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class FriendRequestAdapter(context: Context, private val int: Int, arrayList : ArrayList<String>) : ArrayAdapter<String>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val username = getItem(position)!!
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)

        val acceptFriendRequestBtn = view.findViewById<ImageView>(R.id.acceptFriendRequestBtn)
        val declineFriendRequestBtn = view.findViewById<ImageView>(R.id.declineFriendRequestBtn)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.usernameTextView)

        usernameTextView.text = username

        if (ImageUriListsObject.profilePicsList.contains(username)) {
            if (ImageUriListsObject.getProfilePic(username) != Uri.parse("not_found")) {
                Glide.with(context).load(ImageUriListsObject.getProfilePic(username))
                    .into(profilePicImageView)
            }
        }else {
            Database.getUserProfilePic(username) {
                ImageUriListsObject.setProfilePicImageUriHashMap(username, it)
                if (ImageUriListsObject.getProfilePic(username) != Uri.parse("not_found")) {
                    Glide.with(context).load(it)
                        .into(profilePicImageView)
                }
            }
        }

        acceptFriendRequestBtn.setOnClickListener {
            Database.acceptFriendRequest(username)
        }

        declineFriendRequestBtn.setOnClickListener {
            Database.declineFriendRequest(username)
        }

        return view
    }
}