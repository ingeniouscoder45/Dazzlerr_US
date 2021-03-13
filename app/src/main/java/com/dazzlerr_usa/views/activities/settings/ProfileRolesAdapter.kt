package com.dazzlerr_usa.views.activities.settings

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryfilterItemLayoutBinding
import com.dazzlerr_usa.databinding.SettingsRoleItemLayoutBinding
import timber.log.Timber

class ProfileRolesAdapter(internal var mContext: Context?, internal var mList: ArrayList<HashMap<String , String>>) : androidx.recyclerview.widget.RecyclerView.Adapter<ProfileRolesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder
    {
        val binderObject = DataBindingUtil.inflate<SettingsRoleItemLayoutBinding>(LayoutInflater.from(parent
                .context), R.layout.settings_role_item_layout, parent, false)

        return ViewHolder(binderObject)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int)
    {
       viewHolder.bindingObj.categoryFilterCheckbox.text = mList.get(i).get("role")


        if((mContext as SettingsActivity).primary_role.equals(mList.get(i).get("role") ,ignoreCase = true))
                {
                    viewHolder.bindingObj.categoryFilterCheckbox.isChecked = true
                    viewHolder.bindingObj.categoryFilterCheckbox.isEnabled = false

                }

        if((mContext as SettingsActivity).seconday_role.equals(mList.get(i).get("role") ,ignoreCase = true))
        {
            viewHolder.bindingObj.categoryFilterCheckbox.isChecked = true
            (mContext as SettingsActivity).filter_category_list.add(mList.get(i).get("id")?.toInt()!!)
        }

        viewHolder.bindingObj.categoryFilterCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!(mContext as SettingsActivity).filter_category_list.contains(mList.get(i).get("id")?.toInt()))
                {
                    if((mContext as SettingsActivity).filter_category_list.size<1)
                    {
                       (mContext as SettingsActivity).filter_category_list.add(mList.get(i).get("id")?.toInt()!!)
                       (mContext as SettingsActivity).bindingObj.updateRolesBtn.visibility = View.VISIBLE
                    }

                    else
                    {
                        viewHolder.bindingObj.categoryFilterCheckbox.isChecked = false
                        (mContext as SettingsActivity).showSnackbar("You can select only one secondary role.")
                        //notifyDataSetChanged()
                    }
                }
            }
            else
            {
                if((mContext as SettingsActivity).filter_category_list.contains(mList.get(i).get("id")?.toInt()))
                {
                    (mContext as SettingsActivity).filter_category_list.remove(mList.get(i).get("id")?.toInt()!!)
                    (mContext as SettingsActivity).bindingObj.updateRolesBtn.visibility = View.GONE
                }
            }

            Timber.e((mContext as SettingsActivity).filter_category_list.toString())
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

     inner class ViewHolder(var bindingObj: SettingsRoleItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}
