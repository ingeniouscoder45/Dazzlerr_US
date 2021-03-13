package com.dazzlerr_usa.utilities.imageslider

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * Created by Arvind on 09/7/19
 */
object CheckPermission {

    val PERMISSION_STORAGE = 100

    fun hasPermission(permission: String, context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        } else true
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestPerm(permissions: Array<String>, requestCode: Int, activity: Activity) {
        activity.requestPermissions(permissions, requestCode)
    }
}
