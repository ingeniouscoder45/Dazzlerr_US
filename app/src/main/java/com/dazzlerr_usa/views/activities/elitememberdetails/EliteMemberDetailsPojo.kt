package com.dazzlerr_usa.views.activities.elitememberdetails

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R

@BindingAdapter("eliteImage")
fun loadEliteImage(view: ImageView, imageUrl: String)
{

    Glide.with(view.getContext())
            .load("https://www.dazzlerr.com/API/"+imageUrl).apply(RequestOptions().centerCrop())
            .placeholder(R.drawable.placeholder)
            .into(view)
}
class EliteMemberDetailsPojo {

    var status: Boolean = false
    var success: String? = null
    var result: MutableList<ResultBean>? = null
    var portfolio: MutableList<PortfolioBean>? = null

    class ResultBean {
        var profile_id: Int = 0
        var name: String? = null
        var username: String? = null
        var city: String? = null
        var about: String? = null
        var profile_pic: String? = null
        var desktop_banner: String? = null
        var gallery_banner: String? = null
        var profession: String? = null
        var is_published: String  ? = null
        var views: String ? = ""
        var is_featured: String ? = null
        var likes: String ? = ""
        var like_status: String ? = null
    }

    class PortfolioBean {
        var image: String? = null
    }
}
