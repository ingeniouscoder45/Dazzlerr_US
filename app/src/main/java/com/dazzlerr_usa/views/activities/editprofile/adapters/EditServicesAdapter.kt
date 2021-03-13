package com.dazzlerr_usa.views.activities.editprofile.adapters

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.EditservicesItemLayoutBinding
import com.dazzlerr_usa.databinding.ServicesCategoryTextLayoutBinding
import com.dazzlerr_usa.views.activities.editprofile.EditServicesActivity
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProfileServicesPojo
import timber.log.Timber

class EditServicesAdapter(internal var mContext: Context?, internal var mList: List<ProfileServicesPojo.RoleServicesBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_CATEGORY = 0
    var VIEW_TYPE_NORMAL = 1
    var PREVIOUS_CAT = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        if(viewType==VIEW_TYPE_NORMAL) {
            val binderObject = DataBindingUtil.inflate<EditservicesItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.editservices_item_layout, parent, false)

            return ViewHolder(binderObject)
        }

        else
        {
            val bindingObj : ServicesCategoryTextLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.services_category_text_layout, parent, false)

            return CategoryViewHolder(bindingObj)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {
        if(viewHolder is ViewHolder)
        {

            viewHolder.bindingObj.modelBinder = mList[i]
            viewHolder.bindingObj.executePendingBindings()

            if (mList.get(i).is_added)
            {
                 viewHolder.bindingObj.categoryFilterCheckbox.isChecked = true
                (mContext as EditServicesActivity).filter_services_list.add(mList.get(i).service_name!!)
            }

            viewHolder.bindingObj.categoryFilterCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

                if (isChecked) {
                    if (!(mContext as EditServicesActivity).filter_services_list.contains(mList.get(i).service_name))
                        (mContext as EditServicesActivity).filter_services_list.add(mList.get(i).service_name!!)
                } else {
                    if ((mContext as EditServicesActivity).filter_services_list.contains(mList.get(i).service_name)) {
                        (mContext as EditServicesActivity).filter_services_list.remove(mList.get(i).service_name)
                    }
                }
                Timber.e((mContext as EditServicesActivity).filter_services_list.toString())
            }

        }
        else if(viewHolder is CategoryViewHolder)
        {
            viewHolder.bindingObj.categoryTxt.text = mList.get(i).cat_name
        }
    }


    override fun getItemCount(): Int {
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


    override fun getItemId(position: Int): Long
    {
        return super.getItemId(position)
    }

     inner class ViewHolder(var bindingObj: EditservicesItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class CategoryViewHolder(val bindingObj: ServicesCategoryTextLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
