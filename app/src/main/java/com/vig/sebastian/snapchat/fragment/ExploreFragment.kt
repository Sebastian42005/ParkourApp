package com.vig.sebastian.snapchat.fragment

import android.annotation.SuppressLint
import android.app.ActionBar
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.test.database.Database
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.explore.SearchListAdapter
import kotlin.math.roundToInt
import android.app.Activity
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import com.vig.sebastian.snapchat.explore.ClickedPostObject
import com.vig.sebastian.snapchat.explore.ExploreSearchClass
import com.vig.sebastian.snapchat.explore.ShowPostActivity
import com.vig.sebastian.snapchat.profile.PostType
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedProfileObject
import com.vig.sebastian.snapchat.profile.clicker_profile.ClickedUserProfileActivity


class ExploreFragment : Fragment() {
    lateinit var layoutList: ArrayList<LinearLayout>
    var searchUserList = ArrayList<ExploreSearchClass>()
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explore, container, false)
        val searchEditText : EditText = root.findViewById(R.id.searchEditText)
        val layout1 : LinearLayout = root.findViewById(R.id.layout1)
        val layout2 : LinearLayout = root.findViewById(R.id.layout2)
        val layout3 : LinearLayout = root.findViewById(R.id.layout3)
        val searchListView : ListView = root.findViewById(R.id.searchListView)
        layoutList = arrayListOf(layout1, layout2, layout3)

        setSearchList(root)
        setPostsList()

        searchListView.setOnItemClickListener { parent, view, position, id ->
            Database.getUserInfo(searchUserList[position].username) {user ->
                ClickedProfileObject.user = user
                startActivity(Intent(requireContext(), ClickedUserProfileActivity::class.java))
            }
        }

        searchEditText.setOnTouchListener { v, event ->
            if (!searchListView.isVisible) {
                searchListView.visibility = View.VISIBLE
            }
            return@setOnTouchListener false
        }

        return root
    }
    private fun setPostsList() {
        Database.getExplorePosts { postList ->
            var layoutPosition = 0
            clearAllViews()
            for (post in postList) {
                if (post.postType == PostType.PARKOUR_SPOT) {
                    Database.getImageUriFromUser(post.username, post.key) {
                        val imageView = ImageView(context)
                        Glide.with(requireContext()).load(it).into(imageView)
                        imageView.scaleType = ImageView.ScaleType.FIT_XY
                        val width = (getWidth() / 3).toFloat().roundToInt()
                        val params: ActionBar.LayoutParams = ActionBar.LayoutParams(width, width)
                        if (layoutPosition != 0) {
                            params.leftMargin = 10
                        }
                        imageView.setOnClickListener {
                            ClickedPostObject.uploadPostClass = post
                            startActivity(Intent(context, ShowPostActivity::class.java))
                        }
                        params.bottomMargin = 10
                        imageView.layoutParams = params
                        layoutList[layoutPosition].addView(imageView)
                        if (layoutPosition != 2) layoutPosition++ else layoutPosition = 0
                    }
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
    private fun setSearchList(root: View) {
        Database.getEveryUser { userList ->
            searchUserList = userList
            val adapter = SearchListAdapter(requireContext(), R.layout.search_list_layout, searchUserList)
            val searchListView : ListView = root.findViewById(R.id.searchListView)
            searchListView.adapter = adapter
        }
    }
    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}