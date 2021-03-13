package com.dazzlerr_usa.views.activities.editprofile.adapters

import android.content.Context
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.EditProductsItemLayoutBinding
import com.dazzlerr_usa.views.activities.editprofile.EditEquipmentsActivity
import com.dazzlerr_usa.views.activities.editprofile.pojos.ProductsAndEquipmentsPojo
import timber.log.Timber

class EditEquipmentsAdapter(internal var mContext: Context?, internal var mList: MutableList<ProductsAndEquipmentsPojo.RoleProductsBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<EditEquipmentsAdapter.ViewHolder>()
{


    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder
    {
        val binderObject = DataBindingUtil.inflate<EditProductsItemLayoutBinding>(LayoutInflater.from(parent
                .context), R.layout.edit_products_item_layout, parent, false)

        return ViewHolder(binderObject)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int)
    {


        viewHolder.bindingObj.modelBinder = mList[i]
        viewHolder.bindingObj.executePendingBindings()

        if(mList.get(i).is_added)
                {
                    viewHolder.bindingObj.categoryFilterCheckbox.isChecked = true
                    (mContext as EditEquipmentsActivity).filter_products_list.add(mList.get(i).role_product_id!!)
                }

        viewHolder.bindingObj.categoryFilterCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!(mContext as EditEquipmentsActivity).filter_products_list.contains(mList.get(i).role_product_id))
                    (mContext as EditEquipmentsActivity).filter_products_list.add(mList.get(i).role_product_id!!)
            }

            else
            {
                if((mContext as EditEquipmentsActivity).filter_products_list.contains(mList.get(i).role_product_id))
                {
                    (mContext as EditEquipmentsActivity).filter_products_list.remove(mList.get(i).role_product_id)
                }
            }


            Timber.e((mContext as EditEquipmentsActivity).filter_products_list.toString())
        }
    }

    override fun getItemCount(): Int
    {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

     inner class ViewHolder(var bindingObj: EditProductsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
}
