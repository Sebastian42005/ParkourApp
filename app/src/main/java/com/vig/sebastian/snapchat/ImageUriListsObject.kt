package com.vig.sebastian.snapchat

import android.net.Uri

object ImageUriListsObject {
    private val postImageUriHashMap = HashMap<String, Uri>()
    private var profilePicImageUriHashMap = HashMap<String, Uri?>()
    private var teamPicImageUriHashMap = HashMap<String, Uri?>()
    val profilePicsList = ArrayList<String>()
    val postsList = ArrayList<String>()

    fun setPostImageUriHashMap(key: String, uri: Uri) {
        postsList.add(key)
        postImageUriHashMap[key] = uri
    }

    fun setTeamPicImageUriHashMap(key: String, uri: Uri) {
        teamPicImageUriHashMap[key] = uri
    }

    fun getTeamPic(key: String): Uri? {
        return teamPicImageUriHashMap[key]
    }

    fun setProfilePicImageUriHashMap(key: String, uri: Uri) {
        profilePicsList.add(key)
        profilePicImageUriHashMap[key] = uri
    }
    fun getProfilePic(user: String): Uri? {
        return profilePicImageUriHashMap[user]
    }
    fun getPost(key: String) : Uri? {
        return postImageUriHashMap[key]
    }
}