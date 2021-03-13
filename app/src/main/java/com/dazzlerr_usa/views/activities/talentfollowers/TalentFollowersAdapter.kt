package com.dazzlerr_usa.views.activities.talentfollowers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FollowerAdapterItemLayoutBinding
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.views.activities.OthersProfileActivity

class TalentFollowersAdapter(internal var context: Context?, var mModelList: MutableList<TalentFollowersPojo.DataBean>) : androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
    {
        if (viewType == VIEW_TYPE_LOADING)
        {
            val bindingObj : PaginationLoadingLayoutBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.pagination_loading_layout, parent, false)

            return LoadingViewHolder(bindingObj)
        }

        else
        {
            val binderObject = DataBindingUtil.inflate<FollowerAdapterItemLayoutBinding>(LayoutInflater.from(parent.context), R.layout.follower_adapter_item_layout, parent, false)

            return ViewHolder(binderObject)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, i: Int)
    {
        //animateView(i , viewHolder)
        if(viewHolder is ViewHolder)
        {

            viewHolder.bindingObj.modelBinder = mModelList[i]
            viewHolder.bindingObj.executePendingBindings()

            if(mModelList[i].is_following==1)
            {
                viewHolder.bindingObj.followUnfollowBtn.setBackgroundResource(R.drawable.edittext_unfollow_selector)
                viewHolder.bindingObj.followUnfollowBtn.setTextColor(context?.resources?.getColor(R.color.colorLightGrey1)!!)
                viewHolder.bindingObj.followUnfollowBtn.text = "Unfollow"
            }

            else
            {
                viewHolder.bindingObj.followUnfollowBtn.setBackgroundResource(R.drawable.edittext_follow_selector)
                viewHolder.bindingObj.followUnfollowBtn.setTextColor(context?.resources?.getColor(R.color.colorWhite)!!)
                viewHolder.bindingObj.followUnfollowBtn.text = "Follow"
            }

            viewHolder.bindingObj.followUnfollowBtn.setOnClickListener {

                if(mModelList[i].is_following==0)
                {
                    (context as TalentFollowersActivity).followOrUnfollowUser("1" , i ,  mModelList[i].user_id.toString() )
                }
                else
                {
                    (context as TalentFollowersActivity).followOrUnfollowUser("0" , i ,  mModelList[i].user_id.toString() )
                }
            }

            viewHolder.itemView.setOnClickListener {
                val bundle = Bundle()

                bundle.putString("user_id" , ""+mModelList.get(i).user_id)
                bundle.putString("user_role" , ""+mModelList.get(i).user_role)
                bundle.putString("is_featured" , ""+mModelList.get(i).is_featured)

                context?.startActivity(Intent(context, OthersProfileActivity::class.java).putExtras(bundle))

            }
        }
    }


    override fun onViewDetachedFromWindow(viewHolder: RecyclerView.ViewHolder)
    {
        super.onViewDetachedFromWindow(viewHolder)
        viewHolder.itemView.clearAnimation()
    }

    fun add(response: TalentFollowersPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)

    }

    fun addAll(Items: MutableList<TalentFollowersPojo.DataBean>)
    {
        for (response in Items)
        {
            add(response)
        }
    }

    fun removeAll()
    {

        mModelList.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }


    fun addLoading()
    {
        isLoaderVisible = true
        add(TalentFollowersPojo.DataBean())
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


    fun getItem(position: Int): TalentFollowersPojo.DataBean? {
        return mModelList.get(position)
    }

    override fun getItemId(position: Int): Long {
        val product = mModelList.get(position)
        return product.user_id.toLong()
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

     inner class ViewHolder(var bindingObj: FollowerAdapterItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
     inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}
