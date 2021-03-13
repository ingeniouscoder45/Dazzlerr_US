package com.dazzlerr_usa.views.activities.elitememberdetails

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.imageslider.PreviewFile

class ElitePortfolioAdapter(var context:Context, var mModelList: MutableList<EliteMemberDetailsPojo.PortfolioBean>): androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    val imageList = ArrayList<PreviewFile>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder
    {

        if (p1 == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, p0, false)

            return LoadingViewHolder(bindingObj)
        }

        else {
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            val bindingObj: com.dazzlerr_usa.databinding.ElitePortfolioItemLayoutBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.elite_portfolio_item_layout, p0, false)

            return MyViewHolder(bindingObj)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)
    {
        if(viewHolder is MyViewHolder) {

            Glide.with(context)
                    .load("https://www.dazzlerr.com/API/"+mModelList[position].image)
                    .apply(RequestOptions().centerCrop().fitCenter())
                    .thumbnail(0.3f)
                    .placeholder(R.drawable.placeholder)
                    .into(viewHolder.bindingObj.portfolioImage)

            imageList.add(PreviewFile(mModelList[position].image, false ,false, "" ,"" ,"" ,0 , 0 ,"" ,""))

            viewHolder.itemView.setOnClickListener{

                val intent = Intent(context,
                        ImagePreviewActivity::class.java)

                intent.putExtra(ImagePreviewActivity.IMAGE_LIST,
                        imageList)
                intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, position)
                context.startActivity(intent)

            }


        }
    }

    fun add(response: EliteMemberDetailsPojo.PortfolioBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<EliteMemberDetailsPojo.PortfolioBean>)
    {
        for (response in Items)
        {
            add(response)

        }
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(EliteMemberDetailsPojo.PortfolioBean())
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

    fun removeItem(position: Int)
    {
        mModelList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun getItem(position: Int): EliteMemberDetailsPojo.PortfolioBean? {
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


    inner class MyViewHolder(var bindingObj: com.dazzlerr_usa.databinding.ElitePortfolioItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)


}