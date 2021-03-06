package com.vig.sebastian.snapchat.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.vig.sebastian.snapchat.ImageUriListsObject
import com.vig.sebastian.snapchat.R
import com.vig.sebastian.snapchat.database.Database
import com.vig.sebastian.snapchat.profile.adapter.PostAdapter
import com.vig.sebastian.snapchat.profile.classes.PostClass
import java.lang.Exception

class HomeFragment : Fragment() {
    lateinit var postListView: ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val refreshLayout : androidx.swiperefreshlayout.widget.SwipeRefreshLayout = root.findViewById(R.id.refreshLayout)

        refreshLayout.setColorSchemeColors(Color.rgb(0, 170, 170))
        refreshLayout.setOnRefreshListener {
            setPostsListView(root)
            refreshLayout.isRefreshing = false
        }
        setPostsListView(root)
        return root
    }
    var postList = ArrayList<PostClass>()
    lateinit var adapter : PostAdapter
    private fun setPostsListView(root: View) {
        val noUploadsLayout: LinearLayout = root.findViewById(R.id.noUploadsLayout)
        Database.getFirst10PostsFromFriends {
            if (it.isEmpty()) {
                noUploadsLayout.visibility = View.VISIBLE
            }else noUploadsLayout.visibility = View.GONE
            postList.clear()
            for (post in it) {
                postList.add(PostClass(post.uploadPostClass, post.likeList, ImageUriListsObject.getPost(post.uploadPostClass.key), ImageUriListsObject.getProfilePic(post.uploadPostClass.username)))
            }
            postListView = root.findViewById(R.id.postListView)
            try {
                adapter = PostAdapter(requireContext(), R.layout.post_layout, postList, requireActivity())
                postListView.adapter = adapter
            }catch (e: Exception){}
        }
    }
}