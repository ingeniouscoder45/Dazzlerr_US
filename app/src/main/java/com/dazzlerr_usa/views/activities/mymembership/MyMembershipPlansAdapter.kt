package com.dazzlerr_usa.views.activities.mymembership

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.MembershipPlanItemLayoutBinding
import com.dazzlerr_usa.databinding.MyMembershipPlanItemLayoutBinding
import com.dazzlerr_usa.databinding.MyjobsItemLayoutBinding

class MyMembershipPlansAdapter(val mContext: Context, var mList: MutableList<MyMembershipPojo.Feature>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
            val bindingObj = DataBindingUtil.inflate<MyMembershipPlanItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.my_membership_plan_item_layout, parent, false)
            return MyViewHolder(bindingObj)

    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {
            holder.bindingObj.featureNameTxt.text = mList[position].feature_name

            if(mList[position].feature_value.size==1)
            {
                holder.bindingObj.featureTxt.text = mList[position].feature_value[0].capitalize()

            }
        }
    }


    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun getItemId(position: Int): Long {
        return super.getItemId(position)
    }



    inner class MyViewHolder(var bindingObj: MyMembershipPlanItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}