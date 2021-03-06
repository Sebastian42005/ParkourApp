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
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.vig.sebastian.snapchat.R
import kotlin.math.roundToInt
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.ViewUtils
import androidx.core.view.isVisible
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.vig.sebastian.snapchat.Global
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.explore.*
import com.vig.sebastian.snapchat.parkour_spots.ParkourSpotsActivity
import com.vig.sebastian.snapchat.profile.classes.UploadPostClass
import kotlin.Exception

class ExploreFragment : Fragment() {
    lateinit var layoutList: ArrayList<LinearLayout>
    var searchUserList = ArrayList<ExploreSearchClass>()
    lateinit var searchEditText: EditText
    lateinit var filterList: ArrayList<TextView>
    lateinit var noUploadsLayout: LinearLayout
    var currentFilterType = FilterType.USERNAME
    var postCountryFilter = ""
    var postCityFilter = ""
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n", "ClickableViewAccessibility", "UseSwitchCompatOrMaterialCode")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_explore, container, false)
        searchEditText = root.findViewById(R.id.searchEditText)
        val layout1 : LinearLayout = root.findViewById(R.id.layout1)
        val layout2 : LinearLayout = root.findViewById(R.id.layout2)
        val layout3 : LinearLayout = root.findViewById(R.id.layout3)
        val filterLayout: LinearLayout = root.findViewById(R.id.filterLayout)
        val filterUsername: TextView = root.findViewById(R.id.filterUsernameTextView)
        val filterCountry: TextView = root.findViewById(R.id.filterCountryTextView)
        val filterCity: TextView = root.findViewById(R.id.filterCityTextView)
        val filterAge: TextView = root.findViewById(R.id.filterAgeTextView)
        val saveFilterBtn : Button = root.findViewById(R.id.saveFilterBtn)
        val showFilterRelativeLayout : RelativeLayout = root.findViewById(R.id.setPostsFilterLayout)
        val showFilterLayout : LinearLayout = root.findViewById(R.id.setPostsFilterLinearLayout)
        val countryFilterEditText: EditText = root.findViewById(R.id.filterCountryEditText)
        val cityFilterEditText: EditText = root.findViewById(R.id.filterCityEditText)
        val showFilterBtn : LinearLayout = root.findViewById(R.id.setFilterBtn)
        val onlySpotsSwitch: Switch = root.findViewById(R.id.onlySpotsSwitch)
        val postsListScrollView : ScrollView = root.findViewById(R.id.postsListScrollView)
        val searchSpotBtn: com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton = root.findViewById(R.id.searchSpotsBtn)
        val searchUserBackBtn : ImageView = root.findViewById(R.id.searchUserBackBtn)
        noUploadsLayout = root.findViewById(R.id.noUploadsLayout)
        val refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout = root.findViewById(R.id.refreshLayout)
        filterList = arrayListOf(filterUsername, filterCountry, filterCity, filterAge)
        val searchListView : ListView = root.findViewById(R.id.searchListView)
        layoutList = arrayListOf(layout1, layout2, layout3)

        onlySpotsSwitch.setOnClickListener {
            setPostsList(onlySpotsSwitch.isChecked)
        }

        searchSpotBtn.setOnClickListener{
            try {
                startActivity(Intent(requireContext(), ParkourSpotsActivity::class.java))
            }catch (e: java.lang.Exception) {}
        }

        refreshLayout.setColorSchemeColors(Color.rgb(0, 170, 170))
        refreshLayout.setOnRefreshListener {
            setPostsList(onlySpotsSwitch.isChecked)
            refreshLayout.isRefreshing = false
        }

        setPostsList(onlySpotsSwitch.isChecked)
        setFilter(root)

        showFilterRelativeLayout.setOnClickListener {
            hideKeyboard(requireActivity())
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(showFilterLayout)
            Global.wait(300) {
                showFilterLayout.visibility = View.GONE
                showFilterRelativeLayout.visibility = View.GONE
            }
        }

        showFilterBtn.setOnClickListener {
            if (!showFilterRelativeLayout.isVisible) {
                showFilterRelativeLayout.visibility = View.VISIBLE
                showFilterLayout.visibility = View.VISIBLE
                YoYo.with(Techniques.SlideInUp).duration(300).playOn(showFilterLayout)
            } else {
                hideKeyboard(requireActivity())
                Global.wait(300) {
                    showFilterLayout.visibility = View.GONE
                    showFilterRelativeLayout.visibility = View.GONE
                }
            }
        }

        saveFilterBtn.setOnClickListener {
            hideKeyboard(requireActivity())
            postCountryFilter = countryFilterEditText.text.toString()
            postCityFilter = cityFilterEditText.text.toString()
            showFilterRelativeLayout.visibility = View.GONE
            YoYo.with(Techniques.SlideOutDown).duration(300).playOn(showFilterLayout)
            Global.wait(300) {
                showFilterLayout.visibility = View.GONE
            }
            setPostsList(onlySpotsSwitch.isChecked)
        }
        setSearchList(root)

        searchListView.setOnItemClickListener { parent, view, position, id ->
            Global.showProfile(searchUserList[position].username, context)
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                setSearchList(root)
            }
        })

        searchEditText.setOnTouchListener { v, event ->
            searchListView.visibility = View.VISIBLE
            filterLayout.visibility = View.VISIBLE
            showFilterBtn.visibility = View.GONE
            onlySpotsSwitch.visibility = View.GONE
            showFilterLayout.visibility = View.GONE
            postsListScrollView.visibility = View.GONE
            searchUserBackBtn.visibility = View.VISIBLE
            return@setOnTouchListener false
        }

        searchUserBackBtn.setOnClickListener {
            searchListView.visibility = View.GONE
            filterLayout.visibility = View.GONE
            postsListScrollView.visibility = View.VISIBLE
            searchEditText.setText("")
            onlySpotsSwitch.visibility = View.VISIBLE
            showFilterBtn.visibility = View.VISIBLE
            searchUserBackBtn.visibility = View.GONE
            hideKeyboard(requireActivity())
            searchEditText.clearFocus()
        }

        return root
    }

    private fun setFilter(root: View) {
        for (filterTextView in filterList) {
            filterTextView.setOnClickListener {
                for (filter in filterList) {
                    filter.setTypeface(null, Typeface.NORMAL)
                }
                filterTextView.setTypeface(null, Typeface.BOLD)
                currentFilterType = Global.getFilterType(filterTextView.text.toString().toUpperCase())
                setSearchList(root)
            }
        }
    }
    var layoutPosition = 0
    private fun setPostsList(onlySpots: Boolean) {
        Database.getExplorePosts(onlySpots, postCountryFilter, postCityFilter) { postList ->
            if (postList.isEmpty()) {
                noUploadsLayout.visibility = View.VISIBLE
            }else noUploadsLayout.visibility = View.GONE
            layoutPosition = 0
            clearAllViews()
            for (post in postList) {
                displayPost(post)
            }
        }
    }

    private fun displayPost(post: UploadPostClass) {
            val imageView = ImageView(context)
            Glide.with(requireContext()).load(ImageUriListsObject.getPost(post.key))
                .into(imageView)
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            val width = (getWidth() / 3).toFloat().roundToInt()
            val params: ActionBar.LayoutParams = ActionBar.LayoutParams(width, width)
            if (layoutPosition != 0) {
                params.leftMargin = 10
            }
            imageView.setOnClickListener {
                ClickedPostObject.uploadPostClass = post
                ClickedPostObject.imageUri = ImageUriListsObject.getPost(post.key)
                startActivity(Intent(context, ShowPostActivity::class.java))
            }
            params.bottomMargin = 10
            imageView.layoutParams = params
            layoutList[layoutPosition].addView(imageView)
            if (layoutPosition != 2) layoutPosition++ else layoutPosition = 0

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
        Database.getEveryUser(searchEditText.text.toString().toLowerCase().trim(), currentFilterType) { userList ->
            searchUserList.clear()
            for (user in userList) {
                searchUserList.add(ExploreSearchClass(user.username, user.importance))
            }
            try {
                val adapter = SearchListAdapter(requireContext(), R.layout.search_list_layout, searchUserList)
                val searchListView: ListView = root.findViewById(R.id.searchListView)
                searchListView.adapter = adapter
            }catch (e: Exception) {}
        }
    }
    private fun hideKeyboard(activity: Activity) {
        val imm = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(requireActivity())
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}