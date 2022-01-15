package com.vig.sebastian.snapchat.profile.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
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
        val likeList = getItem(position)!!.likeList
        val description = getItem(position)!!.uploadPostClass.description
        val country = getItem(position)!!.uploadPostClass.country
        val imageUri = getItem(position)!!.imageUri
        val profileImageUri = getItem(position)!!.profilePicImageUri
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(int, parent, false)
        val postLayout : RelativeLayout = view.findViewById(R.id.postLayout)
        val imageView = view.findViewById<ImageView>(R.id.postImage)
        val profilePicImageView = view.findViewById<ImageView>(R.id.profilePicImageView)
        val likesAmountTextView: TextView = view.findViewById(R.id.postLikeAmountTextView)
        val usernameTextView = view.findViewById<TextView>(R.id.postUsername)
        val likePostBtn: ImageView = view.findViewById(R.id.likePostImageView)
        val descriptionTextView = view.findViewById<TextView>(R.id.postDescriptionTextView)
        val likedImageImageView : ImageView = view.findViewById(R.id.likedImageImageView)
        val optionsBtn : ImageView = view.findViewById(R.id.postOptionsImageView)
        var latitude = 0.0
        var longitude = 0.0

        if (country != "") {
            Database.getLocationFromKey(country, key) {la, lo ->
                latitude = la
                longitude = lo
            }
        }

        optionsBtn.setOnClickListener {
            val popupMenu = PopupMenu(context, optionsBtn)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.shareImageItem -> {
                        shareImage(postType, location, username, description, imageUri)
                        return@OnMenuItemClickListener false
                    }
                    R.id.saveImageItem -> {
                        saveImage(username, key)
                        return@OnMenuItemClickListener false
                    }
                    R.id.deleteImageItem -> {
                        AlertDialog.Builder(context).setMessage(context.getString(R.string.delete_post_question)).setPositiveButton(context.getString(R.string.delete)){_,_->
                            Database.deletePost(key)
                            postLayout.visibility = View.GONE
                        }.setNegativeButton(context.getString(R.string.cancel)) {_,_->}.show()
                        return@OnMenuItemClickListener false
                    }
                    R.id.navigateToSpotItem -> {
                        val gmmIntentUri =
                            Uri.parse("google.navigation:q=$latitude,$longitude&mode=b")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                        return@OnMenuItemClickListener false
                    }
                    R.id.streetViewItem -> {
                        val gmmIntentUri = Uri.parse("google.streetview:cbll=$latitude,$longitude&cbp=0,30,0,0,-15")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        context.startActivity(mapIntent)
                        return@OnMenuItemClickListener false
                    }
                    else -> return@OnMenuItemClickListener false
                }
            })
            if (username != Global.username) {
                if (postType == PostType.PARKOUR_SPOT) {
                    popupMenu.inflate(R.menu.options_spot_menu)
                }else popupMenu.inflate(R.menu.options_post_menu)
            }else {
                if (postType == PostType.PARKOUR_SPOT) {
                    popupMenu.inflate(R.menu.options_own_spot_menu)
                }else popupMenu.inflate(R.menu.options_own_post_menu)
            }
            popupMenu.show()
        }

        imageView.setOnClickListener {
            if(System.currentTimeMillis() - doubleClickLastTime < 300){
                doubleClickLastTime = 0
                if (!likeList.contains(Global.username)) {
                    likePostBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_24))
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
        if (profileImageUri != Uri.parse("not_found")) Glide.with(context).load(profileImageUri).into(profilePicImageView)

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
        if (likeList.contains(Global.username)) likePostBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_24))
        else likePostBtn.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_star_outline_24))
        if (likeList.size != 0) {
            likesAmountTextView.visibility = View.VISIBLE
            if (likeList.size > 1) likesAmountTextView.text = "${likeList.size} Likes" else likesAmountTextView.text = "1 Like"
        }else likesAmountTextView.visibility = View.GONE
        if (imageUri != Uri.parse("not_found")) {
            Glide.with(context).load(imageUri).into(imageView)
        }else {
            Database.getImageUriFromUser(username, key) {
                ImageUriListsObject.setPostImageUriHashMap(key, it)
                Glide.with(context).load(it).into(imageView)
            }
        }

        return view
    }

    private fun saveImage(username: String, key: String) {
        val storageRef = Database.storageReference.child(username).child("Posts").child(key)
        storageRef.downloadUrl.addOnSuccessListener {
            val url = it.toString()
            downloadFile(key, ".jpg", DIRECTORY_DOWNLOADS, url)
        }
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

    private fun downloadFile(fileName: String, fileExtension: String, destinationDirectory: String, url: String) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)

        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDirectory,
            "DCIM/Parkour/$fileName$fileExtension"
        )

        downloadManager.enqueue(request)
    }
}