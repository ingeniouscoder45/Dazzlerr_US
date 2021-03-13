package com.dazzlerr_usa.utilities.customdialogs

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.Window
import android.widget.Button
import com.dazzlerr_usa.R
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.material.bottomsheet.BottomSheetDialog
import timber.log.Timber


class AppUpdateDialog(var con : Context)
{

    var dialog: Dialog? = null
    var  progressBar:SpinKitView ?  = null

    init {
        dialog = BottomSheetDialog(con/*, R.style.NewDialogTheme */)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setCanceledOnTouchOutside(false)
/*
        val window: Window = dialog!!.getWindow()
        val wlp = window.attributes

            wlp.flags = WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        wlp.gravity = Gravity.BOTTOM
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()
        window.attributes = wlp
*/

       /* dialog!!.window.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT)
        //this line is what you need:
        dialog!!.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
*/

        dialog!!.setContentView(R.layout.app_update_dialog_layout)

        val appUpdateNowBtn = dialog!!.findViewById<Button>(R.id.appUpdateNowBtn)

        appUpdateNowBtn.setOnClickListener {

            dialog!!.dismiss()

            val appPackageName = con.packageName // getPackageName() from Context or Activity object
           Timber.e("Package Name: " + appPackageName)
            try {
                con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (anfe: android.content.ActivityNotFoundException) {
                con.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
        dialog!!.show()

    }

}