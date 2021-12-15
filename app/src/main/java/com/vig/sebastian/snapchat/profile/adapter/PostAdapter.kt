package com.vig.sebastian.snapchat.profile.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.like.LikeButton
import com.like.OnLikeListener
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.helper.ResizeAnimation
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.classes.PostClass
import java.util.concurrent.TimeoutException

class PostAdapter(context: Context, private val int: Int, arrayList : ArrayList<PostClass>) : ArrayAdapter<PostClass>(context, int, arrayList){
    private var doubleClickLastTime = 0L
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("ViewHolder", "SetTextI18n", "UseCompatLoadingForDrawables",
        "ClickableViewAccessibility"
    )
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val key = getItem(position)!!.uploadPostClass.key
        val username = getItem(position)!!.uploadPostClass.username
        val postType = getItem(position)!!.uploadPostClass.postType
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
        val likePostBtn: LikeButton = view.findViewById(R.id.likePostImageView)
        val descriptionTextView = view.findViewById<TextView>(R.id.postDescriptionTextView)
        val shareImageBtn: ImageView = view.findViewById(R.id.shareImageImageView)
        val likedImageImageView : ImageView = view.findViewById(R.id.likedImageImageView)
        var likeList = ArrayList<String>()

        shareImageBtn.setOnClickListener {
            val sharingIntent = Intent(Intent.ACTION_SEND)
            sharingIntent.setType("image/*");
            sharingIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
            context.startActivity(Intent.createChooser(sharingIntent, "Share Image Using"))
            /*if (postType == PostType.PARKOUR_SPOT) {
                Global.shareImage(
                    imageUri!!,
                    "Hey, look at this spot!\nSpot location: $location\nUsername: $username\nDescription: $description",
                    context)
            }else {
                Global.shareImage(
                    imageUri!!,
                    "Hey, look at this picture from $username!\nDescription: $description",
                    context)
            }*/
        }

        imageView.setOnTouchListener { v, event ->
            if(System.currentTimeMillis() - doubleClickLastTime < 300){
                doubleClickLastTime = 0
                likedImageImageView.visibility = View.VISIBLE
                Global.wait(300) {
                    likedImageImageView.visibility = View.GONE
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
                }
            }else{
                doubleClickLastTime = System.currentTimeMillis()
            }
            return@setOnTouchListener false
        }

        descriptionTextView.text = description

        profilePicImageView.setOnClickListener {
            Global.showProfile(username, context)
        }

        usernameTextView.text = username
        if (profileImageUri != null) Glide.with(context).load(profileImageUri).into(profilePicImageView)

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

        Glide.with(context).load(imageUri).into(imageView)

        return view
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }
}