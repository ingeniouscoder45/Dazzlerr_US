package com.dazzlerr_usa.utilities

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.Context
import java.text.SimpleDateFormat


object Utils
{
    fun isAppInBackground(context: Context): Boolean
    {
        val myProcess = RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(myProcess)
        val isInBackground = myProcess.importance != RunningAppProcessInfo.IMPORTANCE_FOREGROUND

        return isInBackground
    }

    fun compareDate(date1 : String  , date2: String):Boolean
    {
        var bool =false
        try {
            val sdf =
                    SimpleDateFormat("dd/MM/yyyy")
            val date1 = sdf.parse(date1)
            val date2 = sdf.parse(date2)

            bool = date2.before(date1)

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return bool
    }
}