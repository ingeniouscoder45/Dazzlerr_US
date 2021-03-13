package com.dazzlerr_usa.views.fragments.home.adapters

import android.content.Context
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryItemLayoutBinding
import com.dazzlerr_usa.views.activities.home.HomeActivity
import com.dazzlerr_usa.views.fragments.home.pojos.HomeCategoryPojo
import com.dazzlerr_usa.views.fragments.talent.TalentFragment

class CategoryAdapter(internal var mContext: Context?, internal var mList: List<HomeCategoryPojo.DataBean>) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>()
{

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder
    {
        val binderObject = DataBindingUtil.inflate<CategoryItemLayoutBinding>(LayoutInflater.from(parent
                .context), R.layout.category_item_layout, parent, false)

        return ViewHolder(binderObject)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int)
    {
        viewHolder.bindingObj.modelBinder = mList[i]
        viewHolder.bindingObj.executePendingBindings()
        Glide.with(mContext!!).load("https://www.dazzlerr.com/API/"+mList.get(i).role_img).apply(RequestOptions().centerCrop()).into(viewHolder.bindingObj.categoryImage)

        viewHolder.itemView.setOnClickListener {
            val bundle  = Bundle()
            bundle.putString("category_id" , mList.get(i).user_role_id)

            val fragment = TalentFragment()
            fragment.arguments = bundle
            (mContext as HomeActivity).loadFirstFragment(fragment)
        }
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

     inner class ViewHolder(var bindingObj: CategoryItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}
