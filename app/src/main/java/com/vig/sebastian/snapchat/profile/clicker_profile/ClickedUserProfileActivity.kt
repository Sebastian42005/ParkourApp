package com.vig.sebastian.snapchat.profile.clicker_profile

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.common.internal.GmsLogger
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.chat.user.ClickedChatObject
import com.vig.sebastian.snapchat.chat.user.UserChatActivity
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.explore.ShowPostActivity
import com.vig.sebastian.snapchat.profile.PostObject
import com.vig.sebastian.snapchat.profile.adapter.FriendRequestAdapter
import com.vig.sebastian.snapchat.profile.adapter.PostAdapter
import java.lang.Exception
import kotlin.math.roundToInt

class ClickedUserProfileActivity : AppCompatActivity() {

    lateinit var imageUri: Uri
    lateinit var profilePic: ImageView
    lateinit var layoutList: ArrayList<LinearLayout>
    lateinit var postListLayout: RelativeLayout
    lateinit var backBtn: ImageView
    lateinit var postListView: ListView
    val clickedUser = ClickedProfileObject.user
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clicked_user_profile)

        profilePic = findViewById(R.id.profilePicImageView)
        val profileBanner = findViewById<ImageView>(R.id.profileBannerImageView)
        val profileUsernameTextView = findViewById<TextView>(R.id.profileUsername)
        val profileImageLayout1 = findViewById<LinearLayout>(R.id.layout1)
        val profileImageLayout2 = findViewById<LinearLayout>(R.id.layout2)
        val profileImageLayout3 = findViewById<LinearLayout>(R.id.layout3)
        val profileDescriptionTextView = findViewById<TextView>(R.id.profileDescriptionTextView)
        val followBtn = findViewById<Button>(R.id.followBtn)
        val goToChatActivityBtn : ImageView = findViewById(R.id.goToUserChatActivityBtn)
        val backToMainMenuBtn: ImageView = findViewById(R.id.backToMainMenuBtn)
        postListLayout = findViewById(R.id.profilePostsLayout)
        backBtn = findViewById(R.id.backBtn)
        postListView = findViewById(R.id.postListView)

        backToMainMenuBtn.setOnClickListener {
            super.onBackPressed()
        }

        Database.getFriendsList(clickedUser.username) {
            if (it.contains(Global.username)) {
                followBtn.setBackgroundColor(Color.GRAY)
                followBtn.text = getString(R.string.unfollow)
            }else {
                Database.getFriendRequestList(clickedUser.username) { friendRequestList ->
                    if (friendRequestList.contains(Global.username)) {
                        followBtn.setBackgroundColor(Color.GRAY)
                        followBtn.text = getString(R.string.sent_friend_reqest)
                    }else followBtn.text = getString(R.string.follow)
                }
            }
        }

        goToChatActivityBtn.setOnClickListener {
            ClickedChatObject.username = ClickedProfileObject.user.username
            startActivity(Intent(this, UserChatActivity::class.java))
        }

        followBtn.setOnClickListener {
            if (followBtn.text.toString().toLowerCase() == getString(R.string.unfollow).toLowerCase()) {
                AlertDialog.Builder(this)
                    .setMessage("${getString(R.string.unfollow_user)} ${clickedUser.username}?")
                    .setNegativeButton(getString(R.string.cancel)) {_,_ ->}
                    .setPositiveButton(getString(R.string.unfollow)) { _, _ ->
                        Database.unfollowFriend(clickedUser.username)
                        followBtn.setBackgroundColor(Color.rgb(0, 170, 170))
                        followBtn.text = getString(R.string.follow)
                    }.show()
            }else if (followBtn.text.toString().toLowerCase() == getString(R.string.follow).toLowerCase()){
                Database.getFriendRequestList(Global.username) {
                    if (!it.contains(clickedUser.username)) {
                        Database.sendFriendRequest(clickedUser.username)
                        followBtn.setBackgroundColor(Color.GRAY)
                        followBtn.text = getString(R.string.sent_friend_reqest)
                    }else {
                        YoYo.with(Techniques.Shake).duration(300).playOn(followBtn)
                        Toast.makeText(this, getString(R.string.user_already_sent_friend_request), Toast.LENGTH_SHORT).show()
                    }
                }
            }else {
                Database.removeFriendRequest(clickedUser.username)
                followBtn.setBackgroundColor(Color.rgb(0, 170, 170))
                followBtn.text = getString(R.string.follow)
            }
        }

        setPostsListView()

        layoutList = arrayListOf(profileImageLayout1, profileImageLayout2, profileImageLayout3)

        profileUsernameTextView.text = clickedUser.username
        if (clickedUser.description.trim() == "") {
            profileDescriptionTextView.visibility = View.GONE
        }else profileDescriptionTextView.text = clickedUser.description

        setProfilePicture()
        setPostsList()

        backBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(200).playOn(postListLayout)
            Global.wait(200) {
                postListLayout.visibility = View.GONE
            }
        }
    }
    private fun setProfilePicture() {
        try {
            Database.storageReference.child(clickedUser.username + "/ProfilePic").downloadUrl.addOnSuccessListener {
                Glide.with(this).load(it).into(profilePic)
                ClickedChatObject.imageUri = it
            }.addOnFailureListener {
                profilePic.setBackgroundResource(R.drawable.profile)
            }
        }catch (e: Exception) {}
    }

    private fun setPostsList() {
        val noUploadsLayout : LinearLayout = findViewById(R.id.noUploadsLayout)
        Database.getPostsFromUser(clickedUser.username) {
            var position = 0
            clearAllViews()
            if (it.isEmpty()) noUploadsLayout.visibility = View.VISIBLE
            for (post in it) {
                Database.getImageUriFromUser(clickedUser.username, post.uploadPostClass.key) {
                    try {
                        val imageView = ImageView(this)
                        Glide.with(this).load(it).into(imageView)
                        imageView.scaleType = ImageView.ScaleType.FIT_XY
                        val width = (getWidth() / 3).toFloat().roundToInt()
                        val params: ActionBar.LayoutParams = ActionBar.LayoutParams(width, width)
                        if (position != 0) {
                            params.leftMargin = 10
                        }
                        params.bottomMargin = 10
                        imageView.layoutParams = params
                        imageView.setOnClickListener {
                            ClickedPostObject.uploadPostClass = post.uploadPostClass
                            ClickedPostObject.imageUri = ImageUriListsObject.getPost(post.uploadPostClass.key)
                            startActivity(Intent(this, ShowPostActivity::class.java))
                        }
                        layoutList[position].addView(imageView)
                        if (position >= 2) position = 0 else position++
                    }catch (e: Exception) {}
                }
            }
        }
    }

    private fun clearAllViews() {
        for (layout in layoutList) {
            layout.removeAllViews()
        }
    }

    private fun getWidth() : Int{
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
    lateinit var adapter : PostAdapter
    private fun setPostsListView() {
        Database.getPostsFromUser(clickedUser.username) {
            postListView = findViewById(R.id.postListView)
            try {
                adapter = PostAdapter(this, R.layout.post_layout, it, this)
                postListView.adapter = adapter
            }catch (e: Exception){}
        }
    }
}