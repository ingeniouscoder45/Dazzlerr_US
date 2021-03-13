package com.dazzlerr_usa.views.fragments.talent.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.TalentModelsLayoutBinding
import com.dazzlerr_usa.views.activities.OthersProfileActivity
import com.dazzlerr_usa.views.fragments.home.pojos.ModelsPojo
import com.skydoves.androidribbon.ShimmerRibbonView


class TalentsAdapter(internal var context: Context?,  var mModelList: MutableList<ModelsPojo.DataBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {


        if (viewType == VIEW_TYPE_LOADING)
        {

            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {

            val binderObject = DataBindingUtil.inflate<TalentModelsLayoutBinding>(LayoutInflater.from(parent.context), R.layout.talent_models_layout, parent, false)
            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {
        //animateView(i , viewHolder)

        if(viewHolder is ViewHolder)
        {
            animateView(i , viewHolder)

            var viewHolder : ViewHolder = viewHolder as ViewHolder

            viewHolder.bindingObj.modelBinder = mModelList[i]
            viewHolder.bindingObj.executePendingBindings()


            if(mModelList.get(i).is_trending.equals("0") && mModelList.get(i).toprated.equals("0") && mModelList.get(i).is_featured.equals("0"))
            {
                viewHolder.bindingObj.isFeaturedOrTopRated.visibility = View.GONE
            }

            else if(mModelList.get(i).toprated.equals("1"))
            {
                viewHolder.bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                viewHolder.bindingObj.isFeaturedOrTopRated.setBackgroundResource(R.drawable.ic_top_rated)
            }

            else if(mModelList.get(i).is_featured.equals("1"))
            {
                viewHolder.bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                viewHolder.bindingObj.isFeaturedOrTopRated.setBackgroundResource(R.drawable.ic_featured)
            }

            else if(mModelList.get(i).is_trending.equals("1"))
            {
                viewHolder.bindingObj.isFeaturedOrTopRated.visibility = View.VISIBLE
                viewHolder.bindingObj.isFeaturedOrTopRated.setBackgroundResource(R.drawable.ic_trending)
            }
/*
            else
            setValue(mModelList.get(i).is_featured.toString() ,mModelList.get(i).toprated.toString() ,mModelList.get(i).is_trending.toString() , viewHolder.bindingObj.isFeaturedOrTopRated)
*/

            viewHolder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("user_id" , ""+mModelList.get(i).user_id)
                bundle.putString("user_role" , ""+mModelList.get(i).user_role)
                bundle.putString("is_featured" , ""+mModelList.get(i).is_featured)
                context?.startActivity(Intent(context, OthersProfileActivity::class.java).putExtras(bundle))
            }
        }
    }


    fun setValue(is_featured: String, is_toprated:String , is_trending :String , textView: ShimmerRibbonView)
    {

        if (!is_featured.equals("")&& !is_featured.equals("0"))
        {
                textView.ribbon.text = "Featured"
                textView.ribbon.ribbonBackgroundColor = context?.resources?.getColor(R.color.appColor)!!
                showViews(textView)
        }

        else if (!is_toprated.equals("")&& !is_toprated.equals("0"))
        {
            textView.ribbon.text = "Top Rated"
            textView.ribbon.ribbonBackgroundColor = context?.resources?.getColor(R.color.TopRatedColor)!!
            showViews(textView)
        }

        else if (!is_trending.equals("")&& !is_trending.equals("0"))
        {
            textView.ribbon.text = "Trending"
            textView.ribbon.ribbonBackgroundColor = context?.resources?.getColor(R.color.TrendingColor)!!
            showViews(textView)
        }

        //else hideViews(textLayout)
    }

    private fun showViews(textView: ShimmerRibbonView)
    {
        textView.setVisibility(View.VISIBLE)
    }



    fun animateView(position: Int , viewHolder: RecyclerView.ViewHolder )
    {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context,
                    if (position > lastPosition)
                    {
                        if(position%2==0)
                            R.anim.enter_from_left

                        else
                            R.anim.enter_from_right
                    }
                    else
                        R.anim.exit_to_bottom)
            viewHolder.itemView.startAnimation(animation)
            lastPosition = position
        }
    }



    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }

    fun add(response: ModelsPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)

    }

    fun addAll(Items: MutableList<ModelsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mModelList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(ModelsPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mModelList.size - 1
        val result = getItem(position)

        if (result != null) {
            mModelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): ModelsPojo.DataBean? {
        return mModelList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mModelList.get(position)
        return product.user_id.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mModelList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: TalentModelsLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
