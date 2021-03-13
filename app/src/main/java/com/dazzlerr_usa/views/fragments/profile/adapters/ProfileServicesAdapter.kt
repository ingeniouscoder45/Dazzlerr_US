package com.dazzlerr_usa.views.fragments.profile.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ProfileEquipmentsItemLayoutBinding
import com.dazzlerr_usa.databinding.ServicesCategoryTextLayoutBinding
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo

class ProfileServicesAdapter(val mContext:Context ,internal var mList: List<ProfileServicesPojo.RoleServicesBean>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_CATEGORY = 0
    var VIEW_TYPE_NORMAL = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if(viewType==VIEW_TYPE_NORMAL) {
            val bindingObj: ProfileEquipmentsItemLayoutBinding
            bindingObj = DataBindingUtil.inflate(LayoutInflater.from(parent.context) , R.layout.profile_equipments_item_layout , parent ,false )

            return MyViewHolder(bindingObj)
        }

        else
        {
            val bindingObj : ServicesCategoryTextLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.services_category_text_layout, parent, false)

            return CategoryViewHolder(bindingObj)
        }


    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int
    {
        return if (mList.get(position).shouldChangeCategory)
        {
            VIEW_TYPE_CATEGORY
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        holder.bindingObj.textView.text = mList.get(position).service_name

        else if(holder is CategoryViewHolder)
    {
        holder.bindingObj.categoryTxt.text = mList.get(position).cat_name+":"
    }

    }

    inner class MyViewHolder(var bindingObj: ProfileEquipmentsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class CategoryViewHolder(val bindingObj: ServicesCategoryTextLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}