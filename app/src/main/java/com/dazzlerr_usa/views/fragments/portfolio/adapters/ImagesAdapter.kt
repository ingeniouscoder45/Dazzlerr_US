package com.dazzlerr_usa.views.fragments.portfolio.adapters

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.view.View.OnLongClickListener
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.PaginationLoadingLayoutBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.imageslider.ImagePreviewActivity
import com.dazzlerr_usa.utilities.imageslider.PreviewFile
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.localfragments.PortfolioImagesFragment
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo

class ImagesAdapter(var context:Context , var mModelList: MutableList<PortfolioImagesPojo.DataBean> ,val profile_id :String, val mFragment : PortfolioImagesFragment): androidx.recyclerview.widget.RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    var VIEW_TYPE_LOADING = 0
    var VIEW_TYPE_NORMAL = 1
    private var isLoaderVisible = false
    val imageList = ArrayList<PreviewFile>()
    val selectedItemsToDelete: ArrayList<String> = ArrayList()
    var shouldSelectImage =false

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
            val bindingObj: com.dazzlerr_usa.databinding.ModelsImagesItemLayoutBinding =
                    DataBindingUtil.inflate(layoutInflater, R.layout.models_images_item_layout, p0, false)

            return MyViewHolder(bindingObj)
        }
    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int)
    {

        if(viewHolder is MyViewHolder)
        {
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
                    mFragment.likeOrDislike(mModelList.get(position).user_id.toString() ,profile_id,  "1", mModelList.get(position).portfolio_id.toString() ,position , viewHolder.bindingObj.likeBtn)

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
                    mFragment.heartOrDisheart(mModelList.get(position).user_id.toString(), profile_id ,  "0", mModelList.get(position).portfolio_id.toString() ,position,viewHolder.bindingObj.heartBtn)


            }


            if(mModelList[position].isSelected)
            {
                viewHolder.bindingObj.selectedImageLayout.visibility = View.VISIBLE
            }
            else
                viewHolder.bindingObj.selectedImageLayout.visibility = View.GONE

            (context as PortfolioActivity).bindingObj.selectedItemsTxt.text =selectedItemsToDelete.size.toString()+" images selected"


            viewHolder.itemView.setOnClickListener{

                if(shouldSelectImage)//Selecting images for multiple select and delete
                {
                    if(mModelList[position].isSelected)
                    {

                        if(selectedItemsToDelete.contains(mModelList[position].portfolio_id.toString()))
                        {
                            selectedItemsToDelete.remove(mModelList[position].portfolio_id.toString())
                        }

                        mModelList[position].isSelected = false
                        notifyDataSetChanged()

                    }
                    else
                    {

                        if(!selectedItemsToDelete.contains(mModelList[position].portfolio_id.toString()))
                        {
                            selectedItemsToDelete.add(mModelList[position].portfolio_id.toString())
                        }

                        mModelList[position].isSelected = true
                        notifyDataSetChanged()
                    }
                }
                else {

                    imageList.clear()

                    for (response in mModelList)
                    {

                        if(!response.image.equals("") && !response.image.toString().trim().equals("null"))
                        {
                            imageList.add(PreviewFile(response.image, true ,false
                                    , response.user_id.toString()
                                    , response.portfolio_id.toString()
                                    , (context as PortfolioActivity).user_id
                                    , response.is_heart
                                    , response.is_like
                                    , response.likes!!
                                    , response.hearts!!
                            ))
                        }
                    }

                    val intent = Intent(context, ImagePreviewActivity::class.java)
                    intent.putExtra(ImagePreviewActivity.IMAGE_LIST, imageList)
                    intent.putExtra(ImagePreviewActivity.CURRENT_ITEM, position)
                    mFragment.startActivityForResult(intent ,1000)

                }

            }

            if(context is PortfolioActivity)
            {
                if (!(context as PortfolioActivity).navigateFrom!!.equals("talentFragment", ignoreCase = true))
                {
                    viewHolder.itemView.setOnLongClickListener(object : OnLongClickListener
                    {
                        override fun onLongClick(v: View?): Boolean
                        {

                            if(!shouldSelectImage)
                            {
                                if(!selectedItemsToDelete.contains(mModelList[position].portfolio_id.toString()))
                                {
                                    selectedItemsToDelete.add(mModelList[position].portfolio_id.toString())
                                }

                                shouldSelectImage = true
                                mModelList[position].isSelected = true
                                showDeleteTitleLayout()
                                notifyDataSetChanged()
                                return true
                            }

                            else
                            return false
                        }
                    })
                }
            }

        }
    }

    fun selectAll()
    {
        shouldSelectImage = true

        selectedItemsToDelete.clear()
        for (i in mModelList.indices)
        {
            selectedItemsToDelete.add(mModelList[i].portfolio_id.toString())
            mModelList[i].isSelected = true

        }
        notifyDataSetChanged()
    }


    fun deselectAll()
    {
        for (i in mModelList.indices)
        {
            selectedItemsToDelete.clear()
            mModelList[i].isSelected = false

        }
        notifyDataSetChanged()
    }

    fun hideDeleteTitleLayout()
    {
        (context as PortfolioActivity).bindingObj.deleteTitleLayout.visibility = View.GONE
        (context as PortfolioActivity).bindingObj.titleLayout.titleLayout.visibility = View.VISIBLE

            shouldSelectImage =false
            //deselectAll()
            selectedItemsToDelete.clear()
    }

    fun showDeleteTitleLayout()
    {
        (context as PortfolioActivity).bindingObj.deleteTitleLayout.visibility = View.VISIBLE
        (context as PortfolioActivity).bindingObj.titleLayout.titleLayout.visibility = View.GONE

        (context as PortfolioActivity).bindingObj.deleteBtn.setOnClickListener {

            if(selectedItemsToDelete.size!=0)
            {
                deleteImages()
            }
            else
            {
                Toast.makeText(context , "Please select image to delete"  , Toast.LENGTH_SHORT).show()
            }
        }

        (context as PortfolioActivity).bindingObj.selectAllCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            selectAll()
            else
                deselectAll()
        }

        (context as PortfolioActivity).bindingObj.closeDeleteTitleBtn.setOnClickListener {

            hideDeleteTitleLayout()
            deselectAll()
        }
    }

    fun deleteImages()
    {
        val builder1 = CustomDialog(context)
        builder1.setTitle("Alert!")
        builder1.setMessage("Are you sure that you want to delete?")

        builder1.setPositiveButton("Yes" , DialogListenerInterface.onPositiveClickListener {

            builder1.dismiss()

            (mFragment as PortfolioImagesFragment).deleteImage(selectedItemsToDelete)
        })

        builder1.setNegativeButton("No", DialogListenerInterface.onNegetiveClickListener { builder1.dismiss() })

        builder1.show()

    }

    fun add(response: PortfolioImagesPojo.DataBean)
    {

        mModelList.add(response)
        notifyItemInserted(mModelList.size - 1)
    }

    fun addAll(Items: MutableList<PortfolioImagesPojo.DataBean>)
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
        add(PortfolioImagesPojo.DataBean())
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
        notifyDataSetChanged()
    }

    fun getItem(position: Int): PortfolioImagesPojo.DataBean? {
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


    inner class MyViewHolder(var bindingObj: com.dazzlerr_usa.databinding.ModelsImagesItemLayoutBinding) : androidx.recyclerview.widget.RecyclerView.ViewHolder(bindingObj.root)
    inner class LoadingViewHolder(val bindingObj: PaginationLoadingLayoutBinding): RecyclerView.ViewHolder(bindingObj.root)


}