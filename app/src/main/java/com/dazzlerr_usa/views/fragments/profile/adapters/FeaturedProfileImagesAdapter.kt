package com.dazzlerr_usa.views.fragments.profile.adapters

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.imageslider.PreviewFile
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.profile.FeaturedProfileFragment

class FeaturedProfileImagesAdapter(var context:Context, var mModelList: MutableList<PortfolioImagesPojo.DataBean>, var profile_id: String, var mFragment: FeaturedProfileFragment): androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{

    val imageList = ArrayList<PreviewFile>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder
    {
            val layoutInflater: LayoutInflater = LayoutInflater.from(context)
            val bindingObj: com.dazzlerr_usa.databinding.ModelsImagesItemLayoutBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.models_images_item_layout, p0, false)

            return MyViewHolder(bindingObj)
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)
    {
        if(viewHolder is MyViewHolder) {
            viewHolder.bindingObj.modelBinder = mModelList[position]
            viewHolder.bindingObj.executePendingBindings()

            Glide.with(context)
                    .load("https://www.dazzlerr.com/API/" + mModelList[position].image ).apply(RequestOptions().centerCrop())
                    .placeholder(R.drawable.placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.bindingObj.portfolioImage)

            if(mModelList.get(position).is_like==0)
            {
                viewHolder.bindingObj.likeBtn.setImageResource(R.drawable.ic_like_deselected)
            }
            else
                viewHolder.bindingObj.likeBtn.setImageResource(R.drawable.ic_like_selected)

            if(mModelList.get(position).is_heart==0)
            {
                viewHolder.bindingObj.heartBtn.setImageResource(R.drawable.ic_heart_outline)
            }
            else
                viewHolder.bindingObj.heartBtn.setImageResource(R.drawable.heart_selected)


            viewHolder.bindingObj.likeBtn.setOnClickListener {
                if(mModelList.get(position).is_like==0)
                {
                    mFragment.likeOrDislike(mModelList.get(position).user_id.toString() ,profile_id,  "1", mModelList.get(position).portfolio_id.toString() ,position ,viewHolder.bindingObj.likeBtn)

                }

                else
                    mFragment.likeOrDislike(mModelList.get(position).user_id.toString(),profile_id,  "0", mModelList.get(position).portfolio_id.toString() ,position ,viewHolder.bindingObj.likeBtn)

            }

            viewHolder.bindingObj.heartBtn.setOnClickListener{

                if(mModelList.get(position).is_heart==0)
                {
                    mFragment.heartOrDisheart(mModelList.get(position).user_id.toString() ,profile_id,  "1", mModelList.get(position).portfolio_id.toString() ,position ,viewHolder.bindingObj.heartBtn)

                }

                else
                    mFragment.heartOrDisheart(mModelList.get(position).user_id.toString(), profile_id ,  "0", mModelList.get(position).portfolio_id.toString() ,position ,viewHolder.bindingObj.heartBtn)


            }

            viewHolder.itemView.setOnClickListener{
                imageList.clear()

                for (response in mModelList)
                {

                    if(!response.image.equals("") && !response.image.toString().trim().equals("null"))
                    {
                        imageList.add(PreviewFile(response.image, true ,false , response.user_id.toString()
                                , response.portfolio_id.toString()
                                , profile_id
                                , response.is_heart
                                , response.is_like
                                , response.likes!!
                                , response.hearts!!
                        ))
                    }
                }

                val intent = Intent(context,
                        ImagePreviewActivity::class.java)

                intent.putExtra(ImagePreviewActivity.IMAGE_LIST,
                        imageList)
                intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, position)
                (mFragment as Fragment).startActivityForResult(intent, 1000)

            }

        }
    }


    override fun getItemCount(): Int
    {
        return mModelList.size/*if(mModelList.size<9)mModelList.size else 8*/
    }

    inner class MyViewHolder(var bindingObj: com.dazzlerr_usa.databinding.ModelsImagesItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
}