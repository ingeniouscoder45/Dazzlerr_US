package com.dazzlerr_usa.views.activities.editprofile.pojos

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dazzlerr_usa.R

@BindingAdapter("setProfileStatsIcon")
fun setProfileStatsIcon(view: ProgressBar, drawable : Drawable)
{
    view.progressDrawable = drawable
}

@BindingAdapter("setProgress")
fun setProgress(view: ProgressBar , progress:Int)
{
    view.setProgress(progress)
}

@BindingAdapter("setTotalProgressIconColor")
fun setTotalProgressIconColor(view: ImageView , percentage: Int)
{
    if(percentage<60) {
        view.setImageResource(R.drawable.ic_error_black_24dp)
    }

    else if(percentage<100) {
        view.setImageResource(R.drawable.ic_help_black_24dp)
    }

    else
    {
        view.setImageResource(R.drawable.ic_correct)
        view.setPadding(4, 4, 4, 4)
    }
}

@BindingAdapter("setTotalProgressColor1")
fun setTotalProgressColor1(view: ImageView , percentage: Int)
{
    if(percentage<60) {
        view.setImageResource(R.drawable.ic_error_black_24dp)
    }

    else if(percentage<80) {
        view.setImageResource(R.drawable.ic_help_black_24dp)
    }

    else
    {
        view.setImageResource(R.drawable.ic_correct)
        view.setPadding(4, 4, 4, 4)
    }
}

@BindingAdapter("setProgressTextColor")
fun setProgresstextColor(view: TextView , percentage: Int)
{
    view.setText(""+percentage+"% of 100%")
    if(percentage<60)
        view.setTextColor(view.context.resources.getColor(R.color.appColor))
    else if(percentage<100)
        view.setTextColor(view.context.resources.getColor(R.color.colorYellow))
    else
        view.setTextColor(view.context.resources.getColor(R.color.colorGreen))


}

@BindingAdapter("setProgressTextColor1")
fun setProgresstextColor1(view: TextView , percentage: Int)
{
    view.setText(""+percentage+"% of 100%")
    if(percentage<60)
        view.setTextColor(view.context.resources.getColor(R.color.appColor))
    else if(percentage<80)
        view.setTextColor(view.context.resources.getColor(R.color.colorYellow))
    else
        view.setTextColor(view.context.resources.getColor(R.color.colorGreen))


}



data class ProfileStatsPojo(
        var appearance: Int,
        var audio: Int,
        var equipments: Int,
        var general: Int,
        var portfolio: Int,
        var profilepic: Int,
        var project: Int,
        var ratetravel: Int,
        var security: Int,
        var services: Int,
        var status: Boolean,
        var success: String,
        var total: Int,
        var video: Int
)