package com.dazzlerr_usa.views.activities.blogs.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.databinding.AllCategoryBlogsItemLayoutBinding
import com.dazzlerr_usa.databinding.BloglistAdapterItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.blogs.activities.BlogDetailsActivity
import com.dazzlerr_usa.views.activities.blogs.pojos.AllCategoryBlogsPojo
import java.text.SimpleDateFormat
import java.util.*


class CommonBlogsAdapter(val mContext:Context, val mList: MutableList<AllCategoryBlogsPojo.Result>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    private var lastPosition = -1
    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val bindingObj: AllCategoryBlogsItemLayoutBinding
            bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.all_category_blogs_item_layout , parent ,false )

            return MyViewHolder(bindingObj)
        }

    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder) {
            holder.bindingObj.binderObj = mList.get(position)
            holder.bindingObj.executePendingBindings()

            simpledateFormat(holder.bindingObj.blogDateTxt, mList.get(position).blog_date.toString())
            //setAnimation(holder, position)

            Glide.with(mContext)
                    .load(mList.get(position).image_url)
                    .apply(RequestOptions().centerCrop().fitCenter())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.event_placeholder)
                    .into(holder.bindingObj.blogImage)

            holder.itemView.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("blog_id", "" + mList.get(position).blog_id)
                mContext?.startActivity(Intent(mContext, BlogDetailsActivity::class.java).putExtras(bundle))
            }

        }
    }


    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    private fun setAnimation(holder: MyViewHolder, position: Int)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            val animation = AnimationUtils.loadAnimation(mContext,
                    if (position > lastPosition)
                        R.anim.enter_from_bottom
                    else
                        R.anim.exit_to_bottom)
            holder.itemView.startAnimation(animation)
            lastPosition = position
        }

    }

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("dd MMM\nyyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " /*+ timeFormatter.format(datetime)*/
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday "/* + timeFormatter.format(datetime)*/
            }
            else
            {
                dateStr = formatter.format(datetime)
            }

            dateStr = formatter.format(datetime)
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

    }

    fun add(response: AllCategoryBlogsPojo.Result)
    {
        mList.add(response)
        notifyItemInserted(mList.size - 1)

    }

    fun addAll(Items: MutableList<AllCategoryBlogsPojo.Result>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mList.clear()
        notifyDataSetChanged()
    }

    fun removeAllItems()
    {
        mList.clear();
        notifyDataSetChanged();

    }

    fun addLoading()
    {
        isLoaderVisible = true
        add(AllCategoryBlogsPojo.Result())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mList.size - 1
        val result = getItem(position)

        if (result != null) {
            mList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): AllCategoryBlogsPojo.Result? {
        return mList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mList.get(position)
        return product.blog_id.toLong()
    }

    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }
    inner class MyViewHolder(var bindingObj: AllCategoryBlogsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}