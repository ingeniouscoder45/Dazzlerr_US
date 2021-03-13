package com.dazzlerr_usa.views.activities.report

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ReportReasonItemLayoutBinding

class ReportReasonsAdapter(val mContext:Context, val mList: MutableList<String>): RecyclerView.Adapter<ReportReasonsAdapter.MyViewHolder>()
{
    var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: ReportReasonItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.report_reason_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.textView.text = mList[position]

        if(lastPosition==position)
        {
            holder.bindingObj.textView.setBackgroundResource(R.drawable.app_btn_selector)
            holder.bindingObj.textView.setTextColor(mContext.resources.getColor(R.color.colorWhite))
            (mContext as ProfileReportActivity).reason = mList[position]
        }

        else
        {
            holder.bindingObj.textView.setBackgroundResource(R.drawable.report_reason_round_border)
            holder.bindingObj.textView.setTextColor(mContext.resources.getColor(R.color.colorBlack))
        }

        holder.bindingObj.textView.setOnClickListener {
            lastPosition = position
            notifyDataSetChanged()

            if((mContext as ProfileReportActivity).mList.get(position).equals("Something else",ignoreCase = true))
            {
                mContext.bindingObj.reportMessageLayout.visibility = View.VISIBLE
                mContext.isOtherReasonVisible =true
            }

            else {
                mContext.bindingObj.reportMessageLayout.visibility = View.GONE
                mContext.isOtherReasonVisible =false
            }
        }

    }

    inner class MyViewHolder(var bindingObj: ReportReasonItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}