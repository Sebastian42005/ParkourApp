package com.vig.sebastian.snapchat

import android.net.Uri

object ImageUriListsObject {
    private val postImageUriHashMap = HashMap<String, Uri>()
    private var profilePicImageUriHashMap = HashMap<String, Uri?>()
    val profilePicsList = ArrayList<String>()
    val postsList = ArrayList<String>()

    fun setPostImageUriHashMap(key: String, uri: Uri) {
        if (!postsList.contains(key)) {
            postsList.add(key)
            postImageUriHashMap[key] = uri
        }
    }

    fun setProfilePicImageUriHashMap(key: String, uri: Uri) {
        if (!profilePicsList.contains(key)) {
            profilePicsList.add(key)
            profilePicImageUriHashMap[key] = uri
        }
    }
    fun getProfilePic(user: String): Uri? {
        return profilePicImageUriHashMap[user]
    }
    fun getPost(key: String) : Uri? {
        return postImageUriHashMap[key]
    }
}