package com.vig.sebastian.snapchat.explore

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database

class ShowPostActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)
        val uploadPostClass = ClickedPostObject.uploadPostClass!!
        val key = uploadPostClass.key
        val username = uploadPostClass.username
        val country = uploadPostClass.country
        val city = uploadPostClass.city
        val location = uploadPostClass.location
        val description = uploadPostClass.description

        val imageView = findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = findViewById<ImageView>(R.id.profilePicImageView)
        val likesAmountTextView: TextView = findViewById(R.id.postLikeAmountTextView)
        val usernameTextView = findViewById<TextView>(R.id.postUsername)
        val likePostBtn: ImageView = findViewById(R.id.likePostImageView)
        val descriptionTextView = findViewById<TextView>(R.id.postDescriptionTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        var likeList = ArrayList<String>()

        profilePicImageView.setOnClickListener {
            Global.showProfile(username, this)
        }

        descriptionTextView.text = description

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        usernameTextView.text = username
        Glide.with(this).load(ClickedPostObject.imageUri).into(profilePicImageView)

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

        Database.getPostLikeList(key) { likesList ->
            likeList = likesList
            if (likeList.contains(Global.username)) likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_24) else likePostBtn.setBackgroundResource(R.drawable.ic_baseline_star_outline_24)
            if (likeList.size != 0) {
                likesAmountTextView.visibility = View.VISIBLE
                if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
            }else likesAmountTextView.visibility = View.GONE
        }

        Database.getImageUriFromUser(username, key) { uri ->
            Glide.with(this).load(uri).into(imageView)
        }

    }
}