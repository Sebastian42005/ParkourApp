package com.vig.sebastian.snapchat.explore

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.PostType
import java.util.*


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
        var latitude = 0.0
        var longitude = 0.0
        if (country != "") {
            Database.getLocationFromKey(country, key) {la, lo ->
                latitude = la
                longitude = lo
            }
        }

        val imageView = findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = findViewById<ImageView>(R.id.profilePicImageView)
        val likesAmountTextView: TextView = findViewById(R.id.postLikeAmountTextView)
        val usernameTextView = findViewById<TextView>(R.id.postUsername)
        val likePostBtn: ImageView = findViewById(R.id.likePostImageView)
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
                    R.id.navigateToSpotItem -> {
                        val gmmIntentUri =
                            Uri.parse("google.navigation:q=$latitude,$longitude&mode=b")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                        return@OnMenuItemClickListener false
                    }
                    R.id.streetViewItem -> {
                        val gmmIntentUri =
                            Uri.parse("google.streetview:cbll=$latitude,$longitude&cbp=0,30,0,0,-15")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        startActivity(mapIntent)
                        return@OnMenuItemClickListener false
                    }
                    else -> return@OnMenuItemClickListener false
                }
            })
            if (username != Global.username) {
                if (uploadPostClass.postType == PostType.PARKOUR_SPOT) {
                    popupMenu.inflate(R.menu.options_spot_menu)
                }else popupMenu.inflate(R.menu.options_post_menu)
            }else {
                if (uploadPostClass.postType == PostType.PARKOUR_SPOT) {
                    popupMenu.inflate(R.menu.options_own_spot_menu)
                }else popupMenu.inflate(R.menu.options_own_post_menu)
            }
            popupMenu.show()
        }

        imageView.setOnClickListener {
            if(System.currentTimeMillis() - doubleClickLastTime < 300){
                doubleClickLastTime = 0
                likedImageImageView.visibility = View.VISIBLE
                if (!likeList.contains(Global.username)) {
                    likePostBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_24))
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
        if (ImageUriListsObject.getProfilePic(ClickedPostObject.uploadPostClass!!.username) != Uri.parse("not_found")) {
            Glide.with(this)
                .load(ImageUriListsObject.getProfilePic(ClickedPostObject.uploadPostClass!!.username))
                .into(profilePicImageView)
        }

        likePostBtn.setOnClickListener{
            if (likeList.contains(Global.username)) {
                Database.likePost(key)
                likeList.add(Global.username)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }else {
                Database.removeLikeFromPost(key)
                likeList.remove(Global.username)

                if (likeList.size != 0) {
                    likesAmountTextView.visibility = View.VISIBLE
                    if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
                }else likesAmountTextView.visibility = View.GONE
            }
        }

        Database.getPostLikeList(key) { likesList ->
            likeList = likesList
            if (likeList.contains(Global.username)) likePostBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_24))
            else likePostBtn.setImageDrawable(getDrawable(R.drawable.ic_baseline_star_outline_24))
            if (likeList.size != 0) {
                likesAmountTextView.visibility = View.VISIBLE
                if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
            }else likesAmountTextView.visibility = View.GONE
        }

        if (imageUri != Uri.parse("not_found")) {
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