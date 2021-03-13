package com.dazzlerr_usa.views.activities.myJobs

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.CastingmyjobsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.addjob.AddOrEditJobActivity
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import java.text.SimpleDateFormat

class MyJobsAdapter(val mContext: Context, var mModelList: MutableList<CastingMyJobPojo.DataBean>) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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
            val bindingObj = DataBindingUtil.inflate<CastingmyjobsItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.castingmyjobs_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }
        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {
            holder.bindingObj.jobsBinder = mModelList.get(position)
            holder.bindingObj.executePendingBindings()

            dateFormat(holder.bindingObj.date , mModelList[position].start_date.toString())

            if(mModelList.get(position).is_active.toString().equals("0"))
            {

                    holder.bindingObj.status.text = "Waiting for approval"
                    holder.bindingObj.status.setBackgroundTintList(ColorStateList.valueOf(mContext.resources.getColor(R.color.skyBlueColor)))
                    holder.bindingObj.jobPauseOrActiveBtn.visibility = View.GONE
                    holder.bindingObj.jobCompleteBtn.visibility = View.GONE
                    holder.bindingObj.editJobBtn.visibility = View.VISIBLE
            }

            else if(mModelList.get(position).is_active.toString().equals("1"))
            {
                holder.bindingObj.editJobBtn.visibility = View.GONE
                if (mModelList.get(position).status.equals("0"))
                {
                    holder.bindingObj.status.text = "Pause"
                    holder.bindingObj.status.setBackgroundTintList(ColorStateList.valueOf(mContext.resources.getColor(R.color.colorYellow)))
                    holder.bindingObj.jobPauseOrActiveBtn.text = "Active"
                    holder.bindingObj.jobPauseOrActiveBtn.visibility = View.VISIBLE
                    holder.bindingObj.jobCompleteBtn.visibility = View.VISIBLE
                }
                else if (mModelList.get(position).status.equals("1")) {
                    holder.bindingObj.status.text = "Active"
                    holder.bindingObj.status.setBackgroundTintList(ColorStateList.valueOf(mContext.resources.getColor(R.color.colorGreen)))
                    holder.bindingObj.jobPauseOrActiveBtn.text = "Pause"
                    holder.bindingObj.jobPauseOrActiveBtn.visibility = View.VISIBLE
                    holder.bindingObj.jobCompleteBtn.visibility = View.VISIBLE
                }
                else if (mModelList.get(position).status.equals("2")) {
                    holder.bindingObj.status.text = "Completed"
                    holder.bindingObj.status.setBackgroundTintList(ColorStateList.valueOf(mContext.resources.getColor(R.color.TopRatedColor)))
                    holder.bindingObj.jobPauseOrActiveBtn.visibility = View.GONE
                    holder.bindingObj.jobCompleteBtn.visibility = View.GONE
                }
            }

            else
            {
                holder.bindingObj.editJobBtn.visibility = View.GONE
                holder.bindingObj.status.text = "Rejected"
                holder.bindingObj.status.setBackgroundTintList(ColorStateList.valueOf(mContext.resources.getColor(R.color.appColor)))
                holder.bindingObj.jobPauseOrActiveBtn.visibility = View.GONE
                holder.bindingObj.jobCompleteBtn.visibility = View.GONE
            }
            holder.bindingObj.jobPauseOrActiveBtn.setOnClickListener {

                if(mModelList.get(position).status.equals("0"))
                (mContext as MyJobsActivity).updateJobStatus(mModelList.get(position).call_id.toString() , "1" , position)

                else if(mModelList.get(position).status.equals("1"))
                    (mContext as MyJobsActivity).updateJobStatus(mModelList.get(position).call_id.toString() , "0" , position)
            }

            holder.bindingObj.jobCompleteBtn.setOnClickListener {

                (mContext as MyJobsActivity).updateJobStatus(mModelList.get(position).call_id.toString() , "2" , position)
            }

            holder.itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("call_id" , ""+mModelList.get(position).call_id)
                mContext?.startActivity(Intent(mContext , JobsDetailsActivity::class.java).putExtras(bundle))

            }

            holder.bindingObj.editJobBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putString("type" , "Edit")
                bundle.putString("call_id" ,mModelList.get(position).call_id.toString())
                val intent = Intent(mContext , AddOrEditJobActivity::class.java)
                intent.putExtras(bundle)
                (mContext as MyJobsActivity).startActivityForResult(intent ,101)
            }
        }
    }

    fun dateFormat(view: TextView, date : String)
    {
        try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            val mDate = dateFormat.parse(date)//You will get date object relative to server/client timezone wherever it is parsed
            val formatter = SimpleDateFormat("MMMM dd, yyyy") //If you need time just put specific format for time like 'HH:mm:ss'
            val dateStr = formatter.format(mDate)
            view.text = dateStr
        }
        catch (e:Exception)
        {
            e.printStackTrace()
            view.text = date
        }
    }

    fun add(response: CastingMyJobPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<CastingMyJobPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)

        }
    }

    fun removeAll()
    {
        val size = mModelList.size
        mModelList.clear();
        notifyItemRangeRemoved(0, size);
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
        add(CastingMyJobPojo.DataBean())
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


    fun getItem(position: Int): CastingMyJobPojo.DataBean? {
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


    inner class MyViewHolder(var bindingObj: CastingmyjobsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}