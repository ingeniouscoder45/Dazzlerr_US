package com.dazzlerr_usa.views.activities.institute

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.AdItemLayoutBinding
import com.dazzlerr_usa.databinding.InstituteItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.institutedetails.InstituteDetailsActivity
import com.google.android.gms.ads.AdRequest
import timber.log.Timber
import javax.inject.Inject


class InstitutesAdapter(internal var context: Context?, var mInstituteList: MutableList<InstitutePojo.Data>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    var VIEW_TYPE_AD = 2
    private var isLoaderVisible = false
    private var lastPosition = -1

    init {
        (context?.applicationContext as MyApplication).myComponent.inject(this@InstitutesAdapter)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {

        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }
        else if(viewType == VIEW_TYPE_AD)
        {
            val binderObject = DataBindingUtil.inflate<AdItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.ad_item_layout, parent, false)

            return AdViewHolder(binderObject)
        }
        else {
            val binderObject = DataBindingUtil.inflate<InstituteItemLayoutBinding>(LayoutInflater.from(parent
                    .context), R.layout.institute_item_layout, parent, false)

            return ViewHolder(binderObject)
        }


    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {


        if(viewHolder is ViewHolder)
        {

        viewHolder.bindingObj.binderObj = mInstituteList[i]
        viewHolder.bindingObj.executePendingBindings()

            Glide.with(context!!)
                    .load("https://www.dazzlerr.com/API/" +mInstituteList.get(i).institute_image)
                    .apply(RequestOptions().centerCrop().fitCenter())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.event_placeholder)
                    .into(viewHolder.bindingObj.instituteImage)


        viewHolder.itemView.setOnClickListener {

            var bundle = Bundle()
            bundle.putString("institute_id" , mInstituteList[i].institute_id)

            context!!.startActivity(Intent(context  , InstituteDetailsActivity::class.java).putExtras(bundle))

        }
        }

        else if(viewHolder is AdViewHolder)
        {
            Timber.e("Show Ad")

            //Initializing Google ads
/*
            val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
            val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
            MobileAds.setRequestConfiguration(configuration)
*/

            //MobileAds.initialize(this) {}

            if(sharedPreferences.getString(Constants.Membership_id).equals("")
                    ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                    || sharedPreferences.getString(Constants.Membership_id).equals("1")
                    || sharedPreferences.getString(Constants.Membership_id).equals("6")
            ) {
            val adRequest = AdRequest.Builder().build()
            viewHolder.bindingObj.adView.loadAd(adRequest)
                }
            //-----------------------
        }
    }



    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }


    fun add(response: InstitutePojo.Data)
    {
        mInstituteList.add(response)
        notifyItemInserted(mInstituteList.size - 1)


        if(mInstituteList.size==5
                && (sharedPreferences.getString(Constants.Membership_id).equals("")
                        ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                        || sharedPreferences.getString(Constants.Membership_id).equals("1")
                        || sharedPreferences.getString(Constants.Membership_id).equals("6")
                        ))
        {
            Timber.e("Add Ad")
            var model = InstitutePojo.Data()
            model.viewType = "Ad"
            mInstituteList.add(model)
            notifyItemInserted(mInstituteList.size - 1)
        }
    }

    fun addAll(Items: MutableList<InstitutePojo.Data>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mInstituteList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mInstituteList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(InstitutePojo.Data())
    }


    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mInstituteList.size - 1
        val result = getItem(position)

        if (result != null)
        {
            mInstituteList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): InstitutePojo.Data? {
        return mInstituteList.get(position)
    }

    override fun getItemId(position: Int): Long {

        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int
    {

        return if (isLoaderVisible)
        {
            if (position == mInstituteList.size - 1)
                VIEW_TYPE_LOADING
            else
            {
                if(mInstituteList.get(position).viewType.equals("Ad",ignoreCase = true))
                {
                    Timber.e("Type Ad")
                    VIEW_TYPE_AD
                }
                else
                {
                    Timber.e("Type View")
                    VIEW_TYPE_NORMAL
                }
            }
        }

        else
        {
            VIEW_TYPE_NORMAL
        }
    }

     inner class ViewHolder(var bindingObj: InstituteItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class AdViewHolder(var bindingObj: AdItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
