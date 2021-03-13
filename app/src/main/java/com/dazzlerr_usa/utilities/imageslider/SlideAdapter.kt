package com.dazzlerr_usa.utilities.imageslider

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.dazzlerr_usa.R
import com.github.chrisbanes.photoview.PhotoView

import timber.log.Timber

class SlideAdapter(private val context: Context, var list: List<PreviewFile>) : PagerAdapter()
{
    private val listener: OnItemClickListener<PreviewFile>? = null
    private var isLikeBtnHitted = false
    private var isLoveBtnHitted = false

    override fun getCount(): Int
    {
        return list.size
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean
    {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any
    {


        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.item_preview, container, false)

        val image = view.findViewById<PhotoView>(R.id.iv_preview)
        val heartBtn = view.findViewById<ImageView>(R.id.heartBtn)
        val likeBtn = view.findViewById<ImageView>(R.id.likeBtn)
        val tvLikeLayout = view.findViewById<LinearLayout>(R.id.tvLikeLayout)
        val lovesStatusTxt = view.findViewById<TextView>(R.id.lovesStatusTxt)
        val likesStatusTxt = view.findViewById<TextView>(R.id.likesStatusTxt)
        val likesTotalTxt = view.findViewById<TextView>(R.id.likesTotalTxt)
        val lovesTotalTxt = view.findViewById<TextView>(R.id.lovesTotalTxt)

        likeBtn.setOnClickListener {

            isLikeBtnHitted =true
            if(list.get(position).is_like==0)
            {
                (context as ImagePreviewActivity).likeOrDislike(list.get(position).profile_id ,list.get(position).user_id ,  "1", list.get(position).portfolio_id ,position , likeBtn)
            }

            else
                (context as ImagePreviewActivity).likeOrDislike(list.get(position).profile_id , list.get(position).user_id ,  "0", list.get(position).portfolio_id ,position ,likeBtn)

        }

        heartBtn.setOnClickListener{

            isLoveBtnHitted = true
            if(list.get(position).is_heart==0)
            {
                (context as ImagePreviewActivity).heartOrDisheart(list.get(position).profile_id ,list.get(position).user_id ,  "1", list.get(position).portfolio_id ,position , heartBtn)
            }

            else
                (context as ImagePreviewActivity).heartOrDisheart(list.get(position).profile_id , list.get(position).user_id ,  "0", list.get(position).portfolio_id ,position , heartBtn)

        }

        try
        {
            Timber.e("Image" + list[position].imageURL!!)
            Glide.with(context)
                    .load("https://www.dazzlerr.com/API/" + list[position].imageURL!!)
                    .placeholder(R.drawable.slider_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image)

            if (!list[position].isLikeEnabled!!)
            {
                tvLikeLayout.visibility = View.GONE
            }
            else
            {
                tvLikeLayout.visibility = View.VISIBLE
            }

            likesTotalTxt.text = list.get(position).total_likes
            lovesTotalTxt.text = list.get(position).total_hearts

            if(list.get(position).is_like==0)
            {
                likeBtn.setImageResource(R.drawable.ic_like_deselected)

                if(isLikeBtnHitted)
                animateImageview(likeBtn)

                likesStatusTxt.text = "Like"

            }

            else
            {
                likeBtn.setImageResource(R.drawable.ic_like_selected)

                if(isLikeBtnHitted)
                animateImageview(likeBtn)

                likesStatusTxt.text = "Liked"
            }

            if(list.get(position).is_heart==0)
            {
                heartBtn.setImageResource(R.drawable.ic_heart_outline)

                if(isLoveBtnHitted)
                animateImageview(heartBtn)
                lovesStatusTxt.text = "Love"
            }

            else
            {
                heartBtn.setImageResource(R.drawable.heart_selected)

                if(isLoveBtnHitted)
                animateImageview(heartBtn)

                lovesStatusTxt.text = "Loved"

            }

            container.addView(view)
        }
        catch (e :Exception)
        {
           e.printStackTrace()
        }

        return view
    }

    fun animateImageview(image : ImageView)
    {

        YoYo.with(Techniques.RubberBand)
                .duration(400)
                .playOn(image)

        isLoveBtnHitted =false
        isLikeBtnHitted =false
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }
}