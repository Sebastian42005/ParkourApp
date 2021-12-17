package com.vig.sebastian.snapchat.profile

import android.net.Uri

object PostObject {
    var position = 0
    var uri: Uri? = null
    var type = PostObjectType.NORMAL
}
enum class PostObjectType {
    INTENT,
    NORMAL
}