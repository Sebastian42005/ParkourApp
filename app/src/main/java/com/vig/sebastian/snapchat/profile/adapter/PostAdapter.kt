package com.vig.sebastian.snapchat.profile.adapter

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.like.LikeButton
import com.like.OnLikeListener
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.classes.PostClass


class PostAdapter(context: Context, private val int: Int, arrayList : ArrayList<PostClass>, val activity: Activity) : ArrayAdapter<PostClass>(context, int, arrayList){
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
        val likedImageImageView : ImageView = view.findViewById(R.id.likedImageImageView)
        var likeList = ArrayList<String>()
        val optionsBtn : ImageView = view.findViewById(R.id.postOptionsImageView)

        optionsBtn.setOnClickListener {
            val popupMenu = PopupMenu(context, optionsBtn)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.shareImageItem -> {
                        shareImage(postType, location, username, description, imageUri)
                        return@OnMenuItemClickListener false
                    }
                    R.id.saveImageItem -> {
                        saveImage(imageView, description)
                        return@OnMenuItemClickListener false
                    }
                    R.id.deleteImageItem -> {
                        Database.deletePost(ClickedPostObject.uploadPostClass!!.key)
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
        if (imageUri != null) {
            Glide.with(context).load(imageUri).into(imageView)
        }else {
            Database.getImageUriFromUser(username, key) {
                ImageUriListsObject.setPostImageUriHashMap(key, it)
                Glide.with(context).load(it).into(imageView)
            }
        }

        return view
    }

    private fun saveImage(imageView: ImageView, description: String) {

    }

    private fun shareImage(postType: PostType, location: String, username: String, description: String, imageUri: Uri?) {
        if (postType == PostType.PARKOUR_SPOT) {
            Global.shareImage(
                imageUri,
                context.getString(R.string.hey_look_at_this_spot) +
                        "\n" + context.getString(R.string.location) + ": " + location +
                        "\n" + context.getString(R.string.show_username) + " " + username +
                        "\n" + context.getString(R.string.show_description) + " "+ description,
                context)
        }else {
            Global.shareImage(
                imageUri,
                context.getString(R.string.hey_look_at_this_picture) +
                        "\n" + context.getString(R.string.show_username) + " " + username,
                context)
        }
    }

    override fun isEnabled(position: Int): Boolean {
        return false
    }
}