package com.dazzlerr_usa.views.activities.editprofile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FlexItemLayoutBinding

class SkillsAndInterestsAdapter(val mContext:Context, val mList: MutableList<String>): RecyclerView.Adapter<SkillsAndInterestsAdapter.MyViewHolder>()
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindingObj.textView.text = "#"+mList.get(position).trim()
        holder.bindingObj.tagsCloseIcon.visibility = View.VISIBLE
        holder.bindingObj.tagsCloseIcon.setOnClickListener {
            removeItem(position)
        }
    }

    fun removeItem(position:Int)
    {
        mList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, mList.size)
    }
    inner class MyViewHolder(var bindingObj: FlexItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}