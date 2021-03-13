package com.dazzlerr_usa.views.fragments.portfolio.pojos

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import java.lang.Exception

@BindingAdapter("porfolioImage")
fun loadPortfolioImage(view: ImageView, imageUrl: String)
{
    try {
        Glide.with(view.getContext())
                .load("https://www.dazzlerr.com/API/" + imageUrl).apply(RequestOptions().centerCrop())
                .placeholder(R.drawable.placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(view)
    }
    catch (e: Exception)
    {
        e.printStackTrace()
    }
}

class PortfolioImagesPojo
{
    var status: String? = null
    var success: Boolean = false
    var data: MutableList<DataBean>? = null

    class DataBean
    {
        var portfolio_id: Int = 0
        var user_id: Int = 0
        var image: String? = null
        var title: Any? = null
        var description: Any? = null
        var is_active: Int = 0
        var created_on: String? = null
        var updated_on: Any? = null
        var deleted_on: Any? = null
        var isSelected: Boolean= false
        var is_like: Int = 0
        var is_heart: Int = 0
        var hearts: String? = null
        var likes: String? = null
    }
}
