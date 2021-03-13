package com.dazzlerr_usa.views.fragments.profile.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ProfileEquipmentsItemLayoutBinding
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo

class ProfileProductsAndEquipmentsAdapter(val mContext:Context, val mList: MutableList<ProductsAndEquipmentsPojo.RoleProductsBean>): RecyclerView.Adapter<ProfileProductsAndEquipmentsAdapter.MyViewHolder>()
{

    var VIEW_TYPE_NORMAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder
    {
        val bindingObj: ProfileEquipmentsItemLayoutBinding
        bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.profile_equipments_item_layout , parent ,false )
        return MyViewHolder(bindingObj)
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    @SuppressLint("DefaultLocale")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int)
    {
        holder.bindingObj.textView.text = mList.get(position).brand_name+" "+mList.get(position).model_name
    }

    inner class MyViewHolder(var bindingObj: ProfileEquipmentsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}