package com.dazzlerr_usa.utilities

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.dazzlerr_usa.R
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat

class DownloadFile
{
    var downloadReference: Long = 0

    fun DownloadData(uri: Uri?, mContext: Context): Long {

        Timber.e(uri?.path)
        // Create request for android download manager
        val downloadManager =
            mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);


        val date= java.util.Date()
        val timeStamp =  SimpleDateFormat("yyyyMMdd_HHmmss").format(date.getTime())


        //Setting title of request
        request.setTitle("IMG_"+timeStamp+".jpg")

        request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, File.separator + mContext.resources.getString(R.string.app_name) + File.separator +  uri?.path.toString().split("/").get(uri?.path.toString().split("/").size-1))

        //Enqueue download and save into referenceId
        downloadReference = downloadManager.enqueue(request)
        return downloadReference
    }
}