package com.vig.sebastian.snapchat.explore

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.like.LikeButton
import com.like.OnLikeListener
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.PostType

class ShowPostActivity : AppCompatActivity() {
    private var doubleClickLastTime = 0L
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_post)
        val uploadPostClass = ClickedPostObject.uploadPostClass!!
        val key = uploadPostClass.key
        val username = uploadPostClass.username
        val imageUri = ClickedPostObject.imageUri
        val country = uploadPostClass.country
        val city = uploadPostClass.city
        val location = uploadPostClass.location
        val description = uploadPostClass.description

        val imageView = findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = findViewById<ImageView>(R.id.profilePicImageView)
        val likesAmountTextView: TextView = findViewById(R.id.postLikeAmountTextView)
        val usernameTextView = findViewById<TextView>(R.id.postUsername)
        val likePostBtn: LikeButton = findViewById(R.id.likePostImageView)
        val likedImageImageView = findViewById<ImageView>(R.id.likedImageImageView)
        val descriptionTextView = findViewById<TextView>(R.id.postDescriptionTextView)
        val backBtn: ImageView = findViewById(R.id.backBtn)
        var likeList = ArrayList<String>()
        val optionsBtn : ImageView = findViewById(R.id.postOptionsImageView)

        optionsBtn.setOnClickListener {
            val popupMenu = PopupMenu(this, optionsBtn)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.shareImageItem -> {
                        shareImage(ClickedPostObject.uploadPostClass!!.postType, location, username, description)
                        return@OnMenuItemClickListener false
                    }
                    R.id.saveImageItem -> {
                        saveImage(imageView, description)
                        return@OnMenuItemClickListener false
                    }
                    R.id.deleteImageItem -> {
                        AlertDialog.Builder(this).setMessage(getString(R.string.delete_post_question)).setPositiveButton(getString(R.string.delete)){ _, _->
                            Database.deletePost(ClickedPostObject.uploadPostClass!!.key)
                            super.onBackPressed()
                        }.setNegativeButton(getString(R.string.cancel)) {_,_->}.show()
                        return@OnMenuItemClickListener false
                    }
                    else -> return@OnMenuItemClickListener false
                }
            })
            if (username != Global.username) {
                popupMenu.inflate(R.menu.options_menu)
            }else popupMenu.inflate(R.menu.options_profile_menu)
            popupMenu.show()
        }

        imageView.setOnClickListener {
            if(System.currentTimeMillis() - doubleClickLastTime < 300){
                doubleClickLastTime = 0
                likedImageImageView.visibility = View.VISIBLE
                if (!likeList.contains(Global.username)) {
                    likePostBtn.isLiked = true
                    Database.likePost(key)
                    likeList.add(Global.username)

                    if (likeList.size != 0) {
                        likesAmountTextView.visibility = View.VISIBLE
                        if (likeList.size > 1) likesAmountTextView.text =
                            "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                    } else likesAmountTextView.visibility = View.GONE
                }
                likedImageImageView.visibility = View.VISIBLE
                Global.wait(300) {
                    likedImageImageView.visibility = View.GONE
                }
            }else{
                doubleClickLastTime = System.currentTimeMillis()
            }
        }

        profilePicImageView.setOnClickListener {
            Global.showProfile(username, this)
        }

        descriptionTextView.text = description

        backBtn.setOnClickListener {
            super.onBackPressed()
        }

        usernameTextView.text = username
        if (ImageUriListsObject.getProfilePic(ClickedPostObject.uploadPostClass!!.username) != null) {
            Glide.with(this)
                .load(ImageUriListsObject.getProfilePic(ClickedPostObject.uploadPostClass!!.username))
                .into(profilePicImageView)
        }

        likePostBtn.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                Database.likePost(key)
                likeList.add(Global.username)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }

            override fun unLiked(likeButton: LikeButton?) {
                Database.removeLikeFromPost(key)
                likeList.remove(Global.username)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }
        })

        Database.getPostLikeList(key) { likesList ->
            likeList = likesList
            likePostBtn.isLiked = likeList.contains(Global.username)
            if (likeList.size != 0) {
                likesAmountTextView.visibility = View.VISIBLE
                if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
            }else likesAmountTextView.visibility = View.GONE
        }

        if (imageUri != null) {
            Glide.with(this).load(imageUri).into(imageView)
        }else {
            if (!ImageUriListsObject.postsList.contains(key)) {
                Database.getImageUriFromUser(username, key) {
                    ImageUriListsObject.setPostImageUriHashMap(key, it)
                    Glide.with(this).load(it).into(imageView)
                }
            }else {
                Glide.with(this).load(ImageUriListsObject.getPost(key)).into(imageView)
            }
        }
    }

    private fun saveImage(imageView: ImageView?, description: String) {

    }

    private fun shareImage(postType: Any, location: String, username: String, description: String) {
        if (postType == PostType.PARKOUR_SPOT) {
            Global.shareImage(
                ClickedPostObject.imageUri,
                getString(R.string.hey_look_at_this_spot) +
                        "\n" + getString(R.string.location) + ": " + location +
                        "\n" + getString(R.string.show_username) + " " + username +
                        "\n" + getString(R.string.show_description) + " "+ description,
                this)
        }else {
            Global.shareImage(
                ClickedPostObject.imageUri,
                getString(R.string.hey_look_at_this_picture) +
                        "\n" + getString(R.string.show_username) + " " + username,
                this)
        }
    }
}