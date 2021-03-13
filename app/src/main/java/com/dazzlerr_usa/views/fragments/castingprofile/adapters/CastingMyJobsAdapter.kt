package com.dazzlerr_usa.views.fragments.castingprofile.adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.MyjobsItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.jobsdetails.JobsDetailsActivity
import com.dazzlerr_usa.views.fragments.castingprofile.localfragments.CastingProfileFragment
import com.dazzlerr_usa.views.fragments.castingprofile.pojo.CastingMyJobPojo
import java.text.SimpleDateFormat

class CastingMyJobsAdapter(val mContext: Context, var mModelList: MutableList<CastingMyJobPojo.DataBean>, val mFragment :Fragment , val is_casting:Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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

        else
        {

            val bindingObj = DataBindingUtil.inflate<MyjobsItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.myjobs_item_layout, parent, false)
            return MyViewHolder(bindingObj)

        }

        }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {
                holder.bindingObj.binderObj = mModelList.get(position)
                holder.bindingObj.executePendingBindings()

                dateFormat(holder.bindingObj.featureJobDateTxt, mModelList[position].start_date.toString())


            holder.bindingObj.viewDetailsBtn.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("call_id" , ""+mModelList.get(position).call_id)
                mContext?.startActivity(Intent(mContext , JobsDetailsActivity::class.java).putExtras(bundle))

            }

            if(!is_casting)
                holder.bindingObj.editJobBtn.visibility = View.GONE

            // We are hiding it for now. Because we don't have need to show edit job for active jobs
            else
                holder.bindingObj.editJobBtn.visibility = View.GONE

            holder.bindingObj.editJobBtn.setOnClickListener {

                if(is_casting)
                {
                    val mDialog = CustomDialog(mContext)
                    mDialog.setTitle("Alert!")
                    mDialog.setMessage("")
                    mDialog.setPositiveButton("Ok" , DialogListenerInterface.onPositiveClickListener {
                        mDialog.dismiss()
                    })

                    mDialog.show()
                }
                (mFragment as CastingProfileFragment).editJob(mModelList.get(position).call_id.toString())
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


    inner class MyViewHolder(var bindingObj: MyjobsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}