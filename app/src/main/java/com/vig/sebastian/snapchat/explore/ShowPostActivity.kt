package com.vig.sebastian.snapchat.explore

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.like.LikeButton
import com.like.OnLikeListener
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.PostType
import uk.co.senab.photoview.PhotoViewAttacher

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
        val optionsListView : ListView = findViewById(R.id.optionsListView)
        val optionsList = arrayListOf("Share Image", "Save Image", "Delete Image")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, optionsList)
        optionsListView.adapter = adapter
        optionsBtn.setOnClickListener {
            if (optionsListView.isVisible) {
                optionsListView.visibility = View.GONE
            }else optionsListView.visibility = View.VISIBLE
        }
        optionsListView.setOnItemClickListener { _, _, listViewPosition, id ->
            when (optionsList[listViewPosition]) {
                "Share Image" -> shareImage(ClickedPostObject.uploadPostClass!!.postType, location, username, description, ClickedPostObject.imageUri!!)
                "Save Image" -> saveImage(imageView, description)
                "Delete Image" -> Database.deletePost(key)
            }
            optionsListView.visibility = View.GONE
        }

        val photoViewAttacher = PhotoViewAttacher(imageView)
        photoViewAttacher.update()

        imageView.setOnTouchListener { v, event ->
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
            return@setOnTouchListener false
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

        Glide.with(this).load(ClickedPostObject.imageUri).into(imageView)

    }

    private fun saveImage(imageView: ImageView?, description: String) {

    }

    private fun shareImage(postType: Any, location: String, username: String, description: String, imageUri: Any) {

    }
}