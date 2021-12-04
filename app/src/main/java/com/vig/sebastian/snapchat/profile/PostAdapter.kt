package com.vig.sebastian.snapchat.profile

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

class PostAdapter(context: Context, private val int: Int, arrayList : ArrayList<String>) : ArrayAdapter<String>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val key = getItem(position)!!
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)
        val usernameTextView = view.findViewById<TextView>(R.id.postUsername)

        usernameTextView.text = Global.username
        Database.getUserProfilePic(Global.username) {
            Glide.with(context).load(it).into(profilePicImageView)
        }

        Database.getImageUriFromUser(Global.username, key) { uri ->
            Glide.with(context).load(uri).into(imageView)
        }

        return view
    }
}