package com.dazzlerr_usa.views.fragments.portfolio.adapters

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.ProjectsItemLayoutBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.activities.portfolio.addproject.PortfolioProjectActivity
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioProjectsFragment
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioProjectsPojo

class ProjectsAdapter(val mContext: Context, var mModelList: MutableList<PortfolioProjectsPojo.DataBean> ,val mFragment :Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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
            val bindingObj = DataBindingUtil.inflate<ProjectsItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.projects_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {
            holder.bindingObj.projectsBinder = mModelList.get(position)
            holder.bindingObj.executePendingBindings()

            if(mContext is PortfolioActivity)
            {
                if (mContext.navigateFrom!!.equals("talentFragment", ignoreCase = true))
                {
                    holder.bindingObj.modifyLayout.visibility = View.GONE
                }

                else
                {
                    holder.bindingObj.modifyLayout.visibility = View.VISIBLE
                }
            }

            holder.bindingObj.projectEditBtn.setOnClickListener {

                val bundle  = Bundle()
                bundle.putString("project_id" , mModelList.get(position).id.toString())
                bundle.putString("project_title" , mModelList.get(position).title)
                bundle.putString("project_role" , mModelList.get(position).role)
                bundle.putString("project_startdate" , mModelList.get(position).start_date)
                bundle.putString("project_enddate" , mModelList.get(position).end_date)
                bundle.putString("project_description" , mModelList.get(position).description)
                val intent = Intent(mContext, PortfolioProjectActivity::class.java)
                intent.putExtras(bundle)
                (mContext as PortfolioActivity).startActivityForResult(intent ,100)

            }

            holder.bindingObj.projectDeleteBtn.setOnClickListener {

               deleteProduct(position)
            }
        }
    }

    fun deleteProduct(position: Int)
    {
        val builder1 = CustomDialog(mContext)
        builder1.setTitle("Alert!")
        builder1.setMessage("Are you sure that you want to delete this project?")

        builder1.setPositiveButton(
                "Yes",DialogListenerInterface.onPositiveClickListener {
            (mFragment as PortfolioProjectsFragment).deleteProject(mModelList.get(position).id.toString() ,position)
        })

        builder1.setNegativeButton("No", DialogListenerInterface.onNegetiveClickListener {
            builder1.dismiss()
        })
        builder1.show()

    }


    fun add(response: PortfolioProjectsPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<PortfolioProjectsPojo.DataBean>)
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
        notifyDataSetChanged()
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(PortfolioProjectsPojo.DataBean())
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


    fun getItem(position: Int): PortfolioProjectsPojo.DataBean? {
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


    inner class MyViewHolder(var bindingObj: ProjectsItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}