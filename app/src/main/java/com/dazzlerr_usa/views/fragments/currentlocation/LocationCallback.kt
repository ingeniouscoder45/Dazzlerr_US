package com.dazzlerr_usa.views.fragments.currentlocation

interface LocationCallback {
    fun onLocationFetch(latitude: Double, longitude: Double, address: String)
}
