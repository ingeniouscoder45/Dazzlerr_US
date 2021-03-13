package com.dazzlerr_usa.utilities.imageslider

import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast


import com.dazzlerr_usa.BuildConfig

import java.io.File
import java.io.FileOutputStream

/**
 * Created by Himangi on 20/4/18
 */
object Util {

    private val workingDirectory: File
        get() {
            val directory = File(Environment.getExternalStorageDirectory(), BuildConfig.APPLICATION_ID)
            if (!directory.exists()) {
                directory.mkdir()
            }
            return directory
        }

    fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        val photo = File(getAppFolder(context),
                SystemClock.currentThreadTimeMillis().toString() + ".jpg")
        try {

            val fos = FileOutputStream(photo.path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            if (photo.exists()) {
                val values = ContentValues()

                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.MediaColumns.DATA, photo.absolutePath)

                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                Toast.makeText(context, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("Picture", "Exception in photoCallback", e)
        }

    }

    fun shareImage(context: Context, bitmap: Bitmap): Uri? {
        val photo = File(getAppFolder(context),
                SystemClock.currentThreadTimeMillis().toString() + ".jpg")
        try {

            val fos = FileOutputStream(photo.path)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()
            if (photo.exists()) {
                val values = ContentValues()

                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                values.put(MediaStore.MediaColumns.DATA, photo.absolutePath)

                return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            }
        } catch (e: Exception) {
            Log.e("Picture", "Exception in photoCallback", e)
        }

        return null
    }

    private fun getAppName(context: Context): String {
        val pm = context.applicationContext.packageManager
        var ai: ApplicationInfo?
        try {
            ai = pm.getApplicationInfo(context.packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            ai = null
        }

        return (if (ai != null) pm.getApplicationLabel(ai) else "(unknown)") as String
    }

    fun getAppFolder(context: Context): File {
        val photoDirectory = File(workingDirectory.absolutePath,
                getAppName(context.applicationContext))
        if (!photoDirectory.exists()) {
            photoDirectory.mkdir()
        }
        return photoDirectory

    }

}

