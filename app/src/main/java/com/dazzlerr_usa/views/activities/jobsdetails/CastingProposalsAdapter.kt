package com.dazzlerr_usa.views.activities.jobsdetails

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.ProposalsItemLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class CastingProposalsAdapter(val mContext: Context, var mModelList: MutableList<GetProposalsPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else {
            val bindingObj = DataBindingUtil.inflate<ProposalsItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.proposals_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }
        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {

            if((mContext as JobsDetailsActivity).intent.extras != null
                    && (mContext as JobsDetailsActivity).intent.extras!!.containsKey("selectedModel")
                    && (mContext as JobsDetailsActivity).intent.extras!!.getString("selectedModel" ,"").equals(mModelList.get(position).user_id))
            {
                holder.bindingObj.proposalsMainLayout.setBackgroundResource(R.drawable.bottom_nav_selector)
            }

            holder.bindingObj.proposalsBinder = mModelList.get(position)
            holder.bindingObj.executePendingBindings()

            simpledateFormat(holder.bindingObj.proposalDateTxt ,mModelList.get(position).created_on.toString())

            Glide.with(mContext)
                    .load("https://www.dazzlerr.com/API/" + mModelList.get(position).profile_pic).apply(RequestOptions().centerInside())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(holder.bindingObj.userImage)


            holder.itemView.setOnClickListener {

                (mContext as JobsDetailsActivity).askCastingChecks(mModelList.get(position) , position)
            }

        }
    }

    fun simpledateFormat(view: TextView, date : String)
    {
        try
        {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            val datetime = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            var dateStr = ""

            val calendar = Calendar.getInstance()
            calendar.time = datetime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter = SimpleDateFormat("hh:mma")

            if (calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Today " + timeFormatter.format(datetime)
            }
            else if (calendar.get(Calendar.YEAR) == yesterday.get(Calendar.YEAR) && calendar.get(Calendar.DAY_OF_YEAR) == yesterday.get(Calendar.DAY_OF_YEAR))
            {
                dateStr = "Yesterday " + timeFormatter.format(datetime)
            }
            else
            {
                dateStr = formatter.format(datetime)
            }
            view.text = dateStr
        }

        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }

    }
    fun add(response: GetProposalsPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<GetProposalsPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)

        }
    }

    fun removeAll()
    {
        val size = mModelList.size
        mModelList.clear()
        notifyItemRangeRemoved(0, size)
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }

    fun removeItem(position: Int)
    {
        mModelList.removeAt(position)
        notifyItemRemoved(position)
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(GetProposalsPojo.DataBean())
    }

    fun removeLoading()
    {
        isLoaderVisible = false

        val position = mModelList.size - 1
        val result = getItem(position)

        if (result != null) {
            mModelList.removeAt(position)
            notifyItemRemoved(position)
        }
    }


    fun getItem(position: Int): GetProposalsPojo.DataBean? {
        return mModelList.get(position)
    }


    override fun getItemViewType(position: Int): Int {

        return if (isLoaderVisible)
        {
            if (position == mModelList.size - 1) VIEW_TYPE_LOADING else VIEW_TYPE_NORMAL
        }
        else
        {
            VIEW_TYPE_NORMAL
        }
    }


    inner class MyViewHolder(var bindingObj: ProposalsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}