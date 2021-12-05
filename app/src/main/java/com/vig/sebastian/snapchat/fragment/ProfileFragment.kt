package com.vig.sebastian.snapchat.fragment

import android.app.ActionBar
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.example.test.database.Database
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.login.LoginActivity
import com.vig.sebastian.snapchat.profile.adapter.PostAdapter
import com.vig.sebastian.snapchat.profile.PostObject
import com.vig.sebastian.snapchat.profile.UploadPostActivity
import com.vig.sebastian.snapchat.profile.adapter.FriendRequestAdapter
import com.vig.sebastian.snapchat.profile.classes.PostClass
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class ProfileFragment : Fragment() {
    lateinit var imageUri: Uri
    lateinit var profilePic: ImageView
    lateinit var layoutList: ArrayList<LinearLayout>
    lateinit var postListLayout: RelativeLayout
    lateinit var backBtn: ImageView
    lateinit var postListView: ListView
    var postImageType = "profile"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        profilePic = root.findViewById(R.id.profilePicImageView)
        val profileBanner = root.findViewById<ImageView>(R.id.profileBannerImageView)
        val profileUsernameTextView = root.findViewById<TextView>(R.id.profileUsername)
        val profileImageLayout1 = root.findViewById<LinearLayout>(R.id.layout1)
        val profileImageLayout2 = root.findViewById<LinearLayout>(R.id.layout2)
        val profileImageLayout3 = root.findViewById<LinearLayout>(R.id.layout3)
        val profileDescriptionTextView = root.findViewById<TextView>(R.id.profileDescriptionTextView)
        val postPicBtn : ImageView = root.findViewById(R.id.postPictureBtn)
        val showSettingsBtn : ImageView = root.findViewById(R.id.showSettingsBtn)
        val showFriendsRequestBtn: ImageView = root.findViewById(R.id.showFriendsRequestsBtn)
        val friendRequestsLayout: RelativeLayout = root.findViewById(R.id.friendsRequestsLayout)
        val friendRequestsBackBtn: ImageView = root.findViewById(R.id.friendsRequestsBackBtn)
        val settingsLayout: LinearLayout = root.findViewById(R.id.settingsLayout)
        val logoutBtn: TextView = root.findViewById(R.id.settingsLogoutTextView)
        val showFriendsTextView: TextView = root.findViewById(R.id.settingsFriendsTextView)
        val showSavedPostsTextView: TextView = root.findViewById(R.id.settingsSavedTextView)
        postListLayout = root.findViewById(R.id.profilePostsLayout)
        backBtn = root.findViewById(R.id.backBtn)
        postListView = root.findViewById(R.id.postListView)

        setPostsListView(root)

        Database.backBtnPressed { currentFragmentEnum ->
            when(currentFragmentEnum) {
                CurrentFragmentEnum.PROFILE_SETTINGS -> {
                    Database.pressBackBtn(CurrentFragmentEnum.NOTHING)
                    YoYo.with(Techniques.SlideOutDown).duration(300).playOn(settingsLayout)
                    Global.wait(300) {
                        settingsLayout.visibility = View.GONE
                    }
                }
                CurrentFragmentEnum.PROFILE_FRIEND_REQUESTS -> {
                    Database.pressBackBtn(CurrentFragmentEnum.NOTHING)
                    YoYo.with(Techniques.SlideOutDown).duration(300).playOn(friendRequestsLayout)
                    Global.wait(300) {
                        friendRequestsLayout.visibility = View.GONE
                    }
                }
                CurrentFragmentEnum.PROFILE_SHOW_POSTS -> {
                    Database.pressBackBtn(CurrentFragmentEnum.NOTHING)
                    YoYo.with(Techniques.SlideOutRight).duration(300).playOn(postListLayout)
                    Global.wait(300) {
                        friendRequestsLayout.visibility = View.GONE
                    }
                }
            }
        }

        Database.getFriendRequestList(Global.username) { friendRequestList ->
            val friendRequestsListView: ListView = root.findViewById(R.id.friendsRequestsListView)
            val friendRequestAdapter = FriendRequestAdapter(requireContext(), R.layout.friend_requests_layout, friendRequestList)
            friendRequestsListView.adapter = friendRequestAdapter
        }

        showSettingsBtn.setOnClickListener {
            settingsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(settingsLayout)
            CurrentFragmentObject.currentFragmentEnum = CurrentFragmentEnum.PROFILE_SETTINGS
        }

        logoutBtn.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        showFriendsRequestBtn.setOnClickListener {
            friendRequestsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(friendRequestsLayout)
            CurrentFragmentObject.currentFragmentEnum = CurrentFragmentEnum.PROFILE_FRIEND_REQUESTS
        }

        friendRequestsBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(postListLayout)
            Global.wait(300) {
                postListLayout.visibility = View.GONE
            }
        }

        layoutList = arrayListOf(profileImageLayout1, profileImageLayout2, profileImageLayout3)

        profileUsernameTextView.text = Global.username
        if (Global.description.trim() == "") {
            profileDescriptionTextView.visibility = View.GONE
        }else profileDescriptionTextView.text = Global.description

        setProfilePicture()
        setPostsList()

        postPicBtn.setOnClickListener {
            postImageType = "picture"
            choosePicture()
        }

        profilePic.setOnClickListener {
            postImageType = "profile"
            choosePicture()
        }

        backBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutRight).duration(200).playOn(postListLayout)
            Global.wait(200) {
                postListLayout.visibility = View.GONE
            }
        }

        return root
    }
    private fun setProfilePicture() {
        try {
            Database.storageReference.child(Global.username + "/ProfilePic").downloadUrl.addOnSuccessListener {
                Glide.with(requireContext()).load(it).into(profilePic)
            }.addOnFailureListener {
                profilePic.setBackgroundResource(R.drawable.profile)
            }
        }catch (e: Exception) {}
    }
    private fun choosePicture() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            imageUri = data.data!!
            uploadPicture()
        }
    }
    private fun uploadPicture() {
        if (postImageType == "profile") {
            Database.storageReference.child(Global.username + "/ProfilePic").putFile(imageUri).addOnSuccessListener {
                setProfilePicture()
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }else {
            PostObject.uri = imageUri
            startActivity(Intent(requireContext(), UploadPostActivity::class.java))
        }
    }
    private fun setPostsList() {
        Database.getPostsFromUser(Global.username) {
            var position = 0
            clearAllViews()
            for (post in it) {
                Database.getImageUriFromUser(Global.username, post.uploadPostClass.key) {
                    try {
                        val imageView = ImageView(requireContext())
                        Glide.with(requireContext()).load(it).into(imageView)
                        imageView.scaleType = ImageView.ScaleType.FIT_XY
                        val width = (getWidth() / 3).toFloat().roundToInt()
                        val params: ActionBar.LayoutParams = ActionBar.LayoutParams(width, width)
                        if (position != 0) {
                            params.leftMargin = 10
                        }
                        params.bottomMargin = 10
                        imageView.layoutParams = params
                        imageView.setOnClickListener {
                            postListLayout.visibility = View.VISIBLE
                            CurrentFragmentObject.currentFragmentEnum = CurrentFragmentEnum.PROFILE_SHOW_POSTS
                            YoYo.with(Techniques.SlideInRight).duration(200).playOn(postListLayout)
                            Global.wait(50) {
                                postListView.setSelection(PostObject.position)
                            }
                            PostObject.position = post.position
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
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics.widthPixels
    }
    var postList = ArrayList<PostClass>()
    lateinit var adapter : PostAdapter
    private fun setPostsListView(root: View) {
        Database.getPostsFromUser(Global.username) {
            postList = it
            postListView = root.findViewById(R.id.postListView)
            try {
                adapter = PostAdapter(requireContext(), R.layout.post_layout, postList)
                postListView.adapter = adapter
            }catch (e: Exception){}
        }
    }
}