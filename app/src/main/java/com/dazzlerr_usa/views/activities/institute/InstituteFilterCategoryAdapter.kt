package com.dazzlerr_usa.views.activities.institute

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CategoryInstituteFilterItemLayoutBinding
import com.dazzlerr_usa.databinding.CategoryJobFilterItemLayoutBinding
import com.dazzlerr_usa.databinding.CategoryfilterItemLayoutBinding
import timber.log.Timber

class InstituteFilterCategoryAdapter(internal var mContext: Context?, internal var mList: List<InstitutePojo.FilterCat>) : androidx.recyclerview.widget.RecyclerView.Adapter<InstituteFilterCategoryAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder
    {
        val binderObject = DataBindingUtil.inflate<CategoryInstituteFilterItemLayoutBinding>(LayoutInflater.from(parent
                .context), R.layout.category_institute_filter_item_layout, parent, false)

        return ViewHolder(binderObject)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int)
    {
        viewHolder.bindingObj.modelBinder = mList[i]
        viewHolder.bindingObj.executePendingBindings()

        if((mContext as InstituteFilterActivity).filter_category_list.contains(mList.get(i).category_id))
                {
                    viewHolder.bindingObj.categoryFilterCheckbox.isChecked = true
                }

        viewHolder.bindingObj.categoryFilterCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!(mContext as InstituteFilterActivity).filter_category_list.contains(mList.get(i).category_id))
                    (mContext as InstituteFilterActivity).filter_category_list.add(mList.get(i).category_id)
            }
            else
            {
                if((mContext as InstituteFilterActivity).filter_category_list.contains(mList.get(i).category_id))
                {
                    (mContext as InstituteFilterActivity).filter_category_list.remove(mList.get(i).category_id)
                }
            }
            Timber.e((mContext as InstituteFilterActivity).filter_category_list.toString())
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

     inner class ViewHolder(var bindingObj: CategoryInstituteFilterItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}
