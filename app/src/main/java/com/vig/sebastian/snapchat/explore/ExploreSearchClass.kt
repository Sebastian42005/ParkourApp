package com.vig.sebastian.snapchat.explore

import android.net.Uri

class ExploreSearchClass(val username: String, val importance: Int) : Comparable<ExploreSearchClass>{
    override fun compareTo(other: ExploreSearchClass): Int {
        return other.importance - importance
    }
}