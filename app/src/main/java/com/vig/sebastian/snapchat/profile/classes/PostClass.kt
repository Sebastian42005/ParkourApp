package com.vig.sebastian.snapchat.profile.classes

import android.net.Uri


class PostClass(val uploadPostClass: UploadPostClass, val position: Int, val imageUri: Uri?, val profilePicImageUri: Uri?): Comparable<PostClass>{
    override fun compareTo(other: PostClass): Int {
        return other.uploadPostClass.key.compareTo(uploadPostClass.key)
    }
}