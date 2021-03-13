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
import com.dazzlerr_usa.databinding.TagsTextviewLayoutBinding
import com.dazzlerr_usa.views.activities.blogs.activities.BlogsByTagsActivity
import com.dazzlerr_usa.views.activities.blogs.pojos.BlogDetailsPojo

class BlogsDetailsTagsAdapter(val mContext:Context, val mList: MutableList<BlogDetailsPojo.Tag>): RecyclerView.Adapter<BlogsDetailsTagsAdapter.MyViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: TagsTextviewLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.tags_textview_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.tagsTxt.text = mList.get(position).name

        holder.itemView.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag_id" , mList.get(position).tag_id.toString())
            bundle.putString("tag_name" , mList.get(position).name.toString())
            mContext.startActivity(Intent(mContext , BlogsByTagsActivity::class.java).putExtras(bundle))
        }
    }

    inner class MyViewHolder(var bindingObj: TagsTextviewLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}