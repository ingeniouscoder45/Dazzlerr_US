package com.dazzlerr_usa.views.activities.blogs.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryItemLayoutBinding
import com.dazzlerr_usa.databinding.FlexItemLayoutBinding
import com.dazzlerr_usa.views.activities.blogs.activities.AllCategoryBlogsActivity
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo

class BlogsDetailsCategoryAdapter(val mContext:Context, val mList: MutableList<BlogDetailsPojo.Category>): RecyclerView.Adapter<BlogsDetailsCategoryAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: FlexItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.flex_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindingObj.textView.text = mList.get(position).name

        holder.itemView.setOnClickListener {

            val bundle = Bundle()
            bundle.putString("cat_id" , mList.get(position).cat_id.toString())
            mContext.startActivity(Intent(mContext , AllCategoryBlogsActivity::class.java).putExtras(bundle))
        }
    }

    inner class MyViewHolder(var bindingObj: FlexItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}