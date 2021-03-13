package com.dazzlerr_usa.views.fragments.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryItemLayoutBinding
import com.dazzlerr_usa.databinding.FlexItemLayoutBinding

class InterestAdapter(val mContext:Context , val mList: MutableList<String>): RecyclerView.Adapter<InterestAdapter.MyViewHolder>()
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
        holder.bindingObj.textView.text = mList.get(position).trim()
    }

    inner class MyViewHolder(var bindingObj: FlexItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}