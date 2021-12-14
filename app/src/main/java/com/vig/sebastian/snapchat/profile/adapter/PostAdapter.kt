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
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.classes.PostClass
import java.util.concurrent.TimeoutException

class PostAdapter(context: Context, private val int: Int, arrayList : ArrayList<PostClass>) : ArrayAdapter<PostClass>(context, int, arrayList){
    @SuppressLint("ViewHolder", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val key = getItem(position)!!.uploadPostClass.key
        val username = getItem(position)!!.uploadPostClass.username
        val country = getItem(position)!!.uploadPostClass.country
        val city = getItem(position)!!.uploadPostClass.city
        val location = getItem(position)!!.uploadPostClass.location
        val description = getItem(position)!!.uploadPostClass.description
        val imageUri = getItem(position)!!.imageUri
        val profileImageUri = getItem(position)!!.profilePicImageUri
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val imageView = view.findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)
        val likesAmountTextView: TextView = view.findViewById(R.id.postLikeAmountTextView)
        val usernameTextView = view.findViewById<TextView>(R.id.postUsername)
        val likePostBtn: ImageView = view.findViewById(R.id.likePostImageView)
        val descriptionTextView = view.findViewById<TextView>(R.id.postDescriptionTextView)
        var likeList = ArrayList<String>()

        descriptionTextView.text = description

        profilePicImageView.setOnClickListener {
            Global.showProfile(username, context)
        }

        usernameTextView.text = username
        if (profileImageUri != null) Glide.with(context).load(profileImageUri).into(profilePicImageView)

        likePostBtn.setOnClickListener {
            if (likeList.contains(Global.username)) {
                Database.removeLikeFromPost(key)
                likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_outline_24)
                likeList.remove(Global.username)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }else {
                Database.likePost(key)
                likeList.add(Global.username)
                likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_24)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }
        }

        Database.getPostLikeList(key) {likesList ->
            likeList = likesList
            if (likeList.contains(Global.username)) likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_24) else likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_outline_24)
            if (likeList.size != 0) {
                likesAmountTextView.visibility = View.VISIBLE
                if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
            }else likesAmountTextView.visibility = View.GONE
        }

        Glide.with(context).load(imageUri).into(imageView)

        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }
}