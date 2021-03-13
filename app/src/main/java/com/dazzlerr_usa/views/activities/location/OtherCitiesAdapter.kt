package com.dazzlerr_usa.views.activities.location

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.LocationItemLayoutBinding
import android.widget.Filter
import android.widget.Filterable
import com.dazzlerr_usa.utilities.Constants

class OtherCitiesAdapter(mContext: Context, list: ArrayList<LocationPojo>) : RecyclerView.Adapter<OtherCitiesAdapter.MyViewholder>(), Filterable
{
    var mContext : Context?= mContext
    var  list: MutableList<LocationPojo>?= list
    var filteredMList:MutableList<LocationPojo>? =  this.list

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewholder
    {

        val bindingObj : LocationItemLayoutBinding  = DataBindingUtil.inflate(LayoutInflater.from(mContext) , R.layout.location_item_layout , parent , false )

        return MyViewholder(bindingObj)
    }

    override fun getItemCount(): Int {

        return filteredMList!!.size
    }

    override fun onBindViewHolder(holder: MyViewholder, position: Int)
    {
        /*holder.bindingObj.locationBinder = filteredMList?.get(position)
        holder.bindingObj.executePendingBindings()*/

        if(filteredMList?.get(position)?.cityName.equals("Popular cities"  ,  ignoreCase = true)!! || filteredMList?.get(position)?.cityName.equals("Other cities" ,ignoreCase = true)!!)
        {
            holder.bindingObj.cityName.visibility = View.GONE
            holder.bindingObj.headerTxt.visibility = View.VISIBLE
            holder.bindingObj.headerTxt.text = filteredMList?.get(position)?.cityName?.toUpperCase()
        }

        else
        {
            holder.bindingObj.cityName.visibility = View.VISIBLE
            holder.bindingObj.cityName.text = filteredMList!!.get(position).cityName
            holder.bindingObj.headerTxt.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            if(!filteredMList?.get(position)?.cityName.equals("Popular cities"  ,  ignoreCase = true)!! && !filteredMList?.get(position)?.cityName.equals("Other cities" ,ignoreCase = true)!!)
            {

                (mContext as LocationActivity).sharedPreferences.saveString(Constants.Latitude, "0.0")
                (mContext as LocationActivity).sharedPreferences.saveString(Constants.Longitude, "0.0")
                (mContext as LocationActivity).sharedPreferences.saveString(Constants.CurrentAddress, filteredMList?.get(position)?.cityName!!)

                (mContext as LocationActivity).finish()
            }

        }
    }

    override fun getItemId(position: Int): Long
    {
        return super.getItemId(position)
    }

    override fun getItemViewType(position: Int): Int
    {
        return super.getItemViewType(position)
    }

    override fun getFilter(): Filter
    {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    filteredMList = list
                } else {
                    val filteredList = ArrayList<LocationPojo>()
                    for (row in list!! ) {
                        if(!row.cityName!!.toLowerCase().contains("other") && !row.cityName!!.toLowerCase().contains("popular")) {
                            if (row.cityName!!.toLowerCase().contains(charString.toLowerCase())) {

                                filteredList.add(row)
                            }
                        }
                    }
                    filteredMList = filteredList
                }
                val filterResults = Filter.FilterResults()
                filterResults.values = filteredMList
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                filteredMList = filterResults.values as ArrayList<LocationPojo>
                notifyDataSetChanged()
            }
        }
    }

    inner class MyViewholder(var bindingObj: LocationItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)

}