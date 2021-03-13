package com.dazzlerr_usa.views.activities.eventdetails

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryItemLayoutBinding
import com.dazzlerr_usa.databinding.ContactNumberItemLayoutBinding
import com.dazzlerr_usa.databinding.FlexItemLayoutBinding

class ContactNumberAdapter(val mContext:Context, val mList: MutableList<String>): RecyclerView.Adapter<ContactNumberAdapter.MyViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: ContactNumberItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.contact_number_item_layout , parent ,false )

        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindingObj.contactNumberTxt.text = mList.get(position).trim()

        holder.itemView.setOnClickListener {

            if(!mList.get(position).trim().equals("") && !mList.get(position).trim().equals("null"))
            {
                try
                {
                    val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mList.get(position).trim(), null))
                    mContext.startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

        }
    }



    inner class MyViewHolder(var bindingObj: ContactNumberItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}