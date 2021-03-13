package com.dazzlerr_usa.extensions

import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.dazzlerr_usa.R

fun Context.isNetworkActive(): Boolean
{
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetworkInfo
    return activeNetwork != null && activeNetwork.isConnected
}

fun Context.isNetworkActiveWithMessage(): Boolean
{
    val isActive = isNetworkActive()

    if (!isActive) {
        Toast.makeText(applicationContext , R.string.error_check_internet_connection , Toast.LENGTH_LONG).show()
    }

    return isActive
}

fun Context.clearNotifications() {
    (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).cancelAll()
}

fun String.capitalizeWords(): String = split(" ").joinToString(" ") { it.capitalize() }


