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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.databinding.VideoItemLayoutBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.YoutubePlayerActivity
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioVideosFragment
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioVideosPojo
import java.util.regex.Pattern

class VideosAdapter(val mContext: Context, var mModelList: MutableList<PortfolioVideosPojo.DataBean> ,val mFragment : Fragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>()
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
            val bindingObj = DataBindingUtil.inflate<VideoItemLayoutBinding>(LayoutInflater.from(mContext), R.layout.video_item_layout, parent, false)
            return MyViewHolder(bindingObj)
        }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int)
    {

        if(holder is MyViewHolder)
        {

            Glide.with(mContext)
                    .load("https://img.youtube.com/vi/"+extractYoutubeId(mModelList.get(position).video_url.toString())+"/0.jpg")
                    .apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.bindingObj.videoThumbnail)

            holder.itemView.setOnClickListener {

                val bundle = Bundle()
                bundle.putString("video_url", extractYoutubeId(mModelList.get(position).video_url.toString()))
                val intent = Intent(mContext, YoutubePlayerActivity::class.java)
                intent.putExtras(bundle)
                mContext.startActivity(intent)

            }

            if(mContext is PortfolioActivity)
            {
                if (!mContext.navigateFrom!!.equals("talentFragment", ignoreCase = true)) {
                    holder.itemView.setOnLongClickListener(object : View.OnLongClickListener {
                        override fun onLongClick(v: View?): Boolean {

                            deleteProduct(position)
                            return true
                        }
                    })
                }
            }

        }
    }

    fun deleteProduct(position: Int)
    {
        val builder1 = CustomDialog(mContext)
        builder1.setTitle("Alert!")
        builder1.setMessage("Are you sure that you want to delete this video?")

        builder1.setPositiveButton(
                "Yes", DialogListenerInterface.onPositiveClickListener {
                    (mFragment as PortfolioVideosFragment).deleteProject(mModelList.get(position).video_id.toString() ,position )
                })

        builder1.setNegativeButton("No", DialogListenerInterface.onNegetiveClickListener {
            builder1.dismiss()
        })

        builder1.show()
    }

    fun add(response: PortfolioVideosPojo.DataBean)
    {
        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<PortfolioVideosPojo.DataBean>)
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

    fun removeItem(position: Int)
    {
        mModelList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int
    {
        return mModelList.size
    }

    fun addLoading()
    {
        isLoaderVisible = true
        add(PortfolioVideosPojo.DataBean())
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


    fun getItem(position: Int): PortfolioVideosPojo.DataBean? {
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

    fun extractYoutubeId(ytUrl: String): String
    {
        var vId: String =""
        val pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(ytUrl)
        if (matcher.matches()) {
            vId = matcher.group(1)
        }
        return vId
    }

    inner class MyViewHolder(var bindingObj: VideoItemLayoutBinding) : RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)

}