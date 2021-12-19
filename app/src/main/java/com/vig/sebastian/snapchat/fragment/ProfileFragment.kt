package com.vig.sebastian.snapchat.fragment

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.android.gms.common.internal.GmsLogger
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.MainActivity
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.explore.ShowPostActivity
import com.vig.sebastian.snapchat.login.LoginActivity
import com.vig.sebastian.snapchat.profile.EditProfileActivity
import com.vig.sebastian.snapchat.profile.adapter.PostAdapter
import com.vig.sebastian.snapchat.profile.PostObject
import com.vig.sebastian.snapchat.profile.PostObjectType
import com.vig.sebastian.snapchat.profile.UploadPostActivity
import com.vig.sebastian.snapchat.profile.adapter.FriendRequestAdapter
import com.vig.sebastian.snapchat.profile.classes.PostClass
import com.vig.sebastian.snapchat.settings.SettingsActivity
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.roundToInt


class ProfileFragment : Fragment() {
    lateinit var sharedPreferences : SharedPreferences
    lateinit var editor : SharedPreferences.Editor
    lateinit var profilePicImageUri : Uri
    lateinit var imageUri: Uri
    lateinit var profilePic: ImageView
    lateinit var layoutList: ArrayList<LinearLayout>
    var postImageType = "profile"
    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        sharedPreferences = requireActivity().application.getSharedPreferences("save", Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        profilePic = root.findViewById(R.id.profilePicImageView)
        val profileBanner = root.findViewById<ImageView>(R.id.profileBannerImageView)
        val profileUsernameTextView = root.findViewById<TextView>(R.id.profileUsername)
        val profileImageLayout1 = root.findViewById<LinearLayout>(R.id.layout1)
        val profileImageLayout2 = root.findViewById<LinearLayout>(R.id.layout2)
        val profileImageLayout3 = root.findViewById<LinearLayout>(R.id.layout3)
        val profileDescriptionTextView = root.findViewById<TextView>(R.id.profileDescriptionTextView)
        val postPicBtn : ImageView = root.findViewById(R.id.postPictureBtn)
        val showFriendsRequestBtn: ImageView = root.findViewById(R.id.showFriendsRequestsBtn)
        val friendRequestsLayout: RelativeLayout = root.findViewById(R.id.friendsRequestsLayout)
        val friendRequestsBackBtn: ImageView = root.findViewById(R.id.friendsRequestsBackBtn)
        val settingsBtn: ImageView = root.findViewById(R.id.settingsBtn)
        val editProfileBtn: Button = root.findViewById(R.id.editProfileBtn)
        val refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout = root.findViewById(R.id.refreshLayout)
        val friendRequestAmountTextView : TextView = root.findViewById(R.id.friendRequestAmountTextView)

        refreshLayout.setColorSchemeColors(Color.rgb(0, 170, 170))
        refreshLayout.setOnRefreshListener {
            setPostsList()
            refreshLayout.isRefreshing = false
        }

        editProfileBtn.setOnClickListener {
            startActivity(Intent(context, EditProfileActivity::class.java))
        }

        Database.getFriendRequestList(Global.username) { friendRequestList ->
            val listSize = friendRequestList.size
            if (listSize > 0){
                friendRequestAmountTextView.visibility = View.VISIBLE
                if (listSize > 9) friendRequestAmountTextView.text = "9+"
                else friendRequestAmountTextView.text = "$listSize"
            }else {
                friendRequestAmountTextView.visibility = View.GONE
                friendRequestAmountTextView.text = "0"
            }
            try {
                val friendRequestsListView: ListView =
                    root.findViewById(R.id.friendsRequestsListView)
                val friendRequestAdapter = FriendRequestAdapter(
                    requireContext(),
                    R.layout.friend_requests_layout,
                    friendRequestList
                )
                friendRequestsListView.adapter = friendRequestAdapter
            }catch (e: Exception) {}
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(context, SettingsActivity::class.java))
        }


        showFriendsRequestBtn.setOnClickListener {
            friendRequestsLayout.visibility = View.VISIBLE
            YoYo.with(Techniques.SlideInUp).duration(300).playOn(friendRequestsLayout)
        }

        friendRequestsBackBtn.setOnClickListener {
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(friendRequestsLayout)
            Global.wait(300) {
                friendRequestsLayout.visibility = View.GONE
            }
        }

        layoutList = arrayListOf(profileImageLayout1, profileImageLayout2, profileImageLayout3)

        profileUsernameTextView.text = Global.username
        if (Global.description.trim() == "") {
            profileDescriptionTextView.visibility = View.GONE
        }else profileDescriptionTextView.text = Global.description

        if (ImageUriListsObject.getProfilePic(Global.username) != null) {
            Glide.with(requireContext()).load(ImageUriListsObject.getProfilePic(Global.username)).into(profilePic)
        }
        setPostsList()

        postPicBtn.setOnClickListener {
            postImageType = "picture"
            choosePicture()
        }

        profilePic.setOnClickListener {
            postImageType = "profile"
            choosePicture()
        }

        return root
    }
    private fun setProfilePicture() {
        Database.getUserProfilePic(Global.username) {
            ImageUriListsObject.setProfilePicImageUriHashMap(Global.username, it)
            Glide.with(requireContext()).load(it).into(profilePic)
        }
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
            }
        }else {
            PostObject.uri = imageUri
            PostObject.type = PostObjectType.NORMAL
            startActivity(Intent(requireContext(), UploadPostActivity::class.java))
        }
    }
    private fun setPostsList(

    ) {
        Database.getPostsFromUser(Global.username) {
            var position = 0
            clearAllViews()
            for (post in it) {
                try {
                    val imageView = ImageView(requireContext())
                    Glide.with(requireContext()).load(ImageUriListsObject.getPost(post.uploadPostClass.key)).into(imageView)
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
                        startActivity(Intent(context, ShowPostActivity::class.java))
                    }
                    layoutList[position].addView(imageView)
                    if (position >= 2) position = 0 else position++
                }catch (e: Exception) {}
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
}