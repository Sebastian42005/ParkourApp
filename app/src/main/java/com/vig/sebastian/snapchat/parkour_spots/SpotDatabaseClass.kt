package com.vig.sebastian.snapchat.parkour_spots

import com.vig.sebastian.snapchat.profile.SpotType

data class SpotDatabaseClass(val latitude: Double, val longitude: Double, val key: String, val description: String, val spotType: SpotType)