package com.dazzlerr_usa.views.fragments.home.adapters


import android.app.Activity
import com.dazzlerr_usa.R
import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo

import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.viewholder.ImageSlideViewHolder

class MainSliderAdapter(val mContext : Activity? , val mBannerList : List<HomeCategoryPojo.BannersBean>) : SliderAdapter()
{

    override fun getItemCount(): Int
    {
        return mBannerList.size
    }

    override fun onBindImageSlide(position: Int, viewHolder: ImageSlideViewHolder)
    {
        viewHolder.bindImageSlide("https://www.dazzlerr.com/API/"+mBannerList.get(position).banner_image , R.drawable.placeholder2, R.drawable.placeholder2)
    }
}
