package com.dazzlerr_usa.views.fragments.portfolio.localfragments


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager

import com.dazzlerr_usa.R
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.adapters.ImagesAdapter
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioImagesModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadImagePojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioImagesPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioImagesView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.imageslider.ImageSliderModel
import com.dazzlerr_usa.utilities.imageslider.ImageSliderPresenter
import com.dazzlerr_usa.utilities.imageslider.ImageSliderView
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import droidninja.filepicker.FilePickerConst
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */

class PortfolioImagesFragment : androidx.fragment.app.Fragment()  , PortfolioImagesView , ImageSliderView
{
    lateinit var bindingObj : com.dazzlerr_usa.databinding.FragmentPortfolioImagesBinding
    var adapter: ImagesAdapter? = null
    lateinit var mPresenter: PortfolioImagesPresenter
    lateinit var mLikesPresenter: ImageSliderPresenter
    val PAGE_START = 1
    private var currentPage = PAGE_START
    private var isLoading = false
    private var currentTotalListCount =0
    private var ImagesList: MutableList<PortfolioImagesPojo.DataBean> = ArrayList()
    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater , R.layout.fragment_portfolio_images, container, false)
        initializations()
        apiCalling( (activity as PortfolioActivity).user_id ,currentPage.toString())
        //pagination()
        return bindingObj.root
    }

    fun initializations()
    {
        (activity?.application as MyApplication).myComponent.inject(this)
        //  bindingObj.imagesRecyclerView.layoutManager = GridLayoutManager(activity, 2) as RecyclerView.LayoutManager?
        mPresenter = PortfolioImagesModel(activity as Activity  , this)
        mLikesPresenter = ImageSliderModel(activity as Activity , this)
        adapter = ImagesAdapter(activity as Activity ,ImagesList ,sharedPreferences.getString(Constants.User_id).toString(), this)
        val gManager = GridLayoutManager(activity, 2)
/*
        gManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup()
        {
            override fun getSpanSize(position: Int): Int
            {
                when (adapter!!.getItemViewType(position))
                {
                    adapter?.VIEW_TYPE_LOADING -> return 2
                    adapter?.VIEW_TYPE_NORMAL -> return 1
                    else -> return -1
                }
            }
        }*/

        bindingObj.imagesRecyclerView.layoutManager = gManager

        val spanCount = 2 // 3 columns
        val spacing = 2 // 4px
        val includeEdge = true
        bindingObj.imagesRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.imagesRecyclerView.adapter = adapter

        (activity as PortfolioActivity).bindingObj.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {}

            override fun onTabUnselected(tab: TabLayout.Tab)
            {

                if (tab.text!!.toString().equals("IMAGES", ignoreCase = true))
                {
                    if(adapter?.shouldSelectImage!!)
                        adapter?.deselectAll()

                }

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

/*    private fun pagination()
    {
        bindingObj.imagesRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val visibleThreshold = 6

                val layoutManager = recyclerView.layoutManager as GridLayoutManager?
                val lastItem = layoutManager!!.findLastCompletelyVisibleItemPosition()
                val currentTotalCount = layoutManager.itemCount


                if (currentTotalCount <= lastItem + visibleThreshold && !isLoading && currentTotalListCount==10)
                {

                    adapter?.addLoading()
                    isLoading = true
                    apiCalling((activity as PortfolioActivity).user_id  , currentPage.toString())
                }
            }
        })
    }*/

    fun deleteImage(selectedItemsToDelete: ArrayList<String>)
    {
        mPresenter.deleteImage((activity as PortfolioActivity).user_id ,selectedItemsToDelete)
    }

    override fun onImageDelete(selectedItemsToDelete: ArrayList<String>)
    {

        for (i in adapter?.mModelList!!.size-1 downTo 0)
        {

            if(adapter?.mModelList!!.get(i).isSelected)
                adapter?.removeItem(i)
        }


        adapter?.hideDeleteTitleLayout()
      //  showSnackbar("Images has been deleted successfully.")

        if(adapter?.mModelList!!.size==0)
            bindingObj.emptyLayout.visibility  = View.VISIBLE
    }

    private fun apiCalling(user_id: String , page:String)
    {
        if(activity?.isNetworkActive()!!)
        {
            mPresenter?.getPortfolioImages(user_id ,sharedPreferences.getString(Constants.User_id).toString() ,  page , "true")
        }

        else
        {

            val dialog  = CustomDialog(activity as Activity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(user_id ,page)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    fun addItem( list : MutableList<PortfolioImagesPojo.DataBean>)
    {
        if (currentPage != PAGE_START)
            adapter?.removeLoading()

        adapter?.addAll(list)
    }

    override fun onSuccess(model: PortfolioImagesPojo)
    {

        if (model.data?.size!=0)
        {
            currentTotalListCount = model.data?.size!!

            ImagesList.clear()
            ImagesList.addAll(model.data!!)
            adapter?.notifyDataSetChanged()
/*            if(currentPage==PAGE_START)
            {
                adapter?.addAll(model.data!!)
                //adapter?.addLoading()
            }
            else
                addItem(model.data!!)

            currentPage++
            isLoading = false*/
            bindingObj.emptyLayout.visibility  = View.GONE
        }
        else
        {

            bindingObj.emptyLayout.visibility  = View.VISIBLE

/*            if(currentPage!=PAGE_START)
                adapter?.removeLoading()

            else if(currentPage==PAGE_START)
            {
                bindingObj.emptyLayout.visibility  = View.VISIBLE
            }*/

        }


    }

    override fun onImageUpload(model: UploadImagePojo)
    {
            val Obj = PortfolioImagesPojo.DataBean()
            Obj.image = model.data?.get(0)?.image
            Obj.user_id = model.data?.get(0)?.user_id!!
            Obj.portfolio_id = model.data?.get(0)?.portfolio_id!!
            Obj.hearts ="0"
            Obj.likes = "0"
            Obj.is_heart = 0
            Obj.is_like = 0

            //   Obj.title =   model.data?.get(0)?.title!!

        var mModelList: MutableList<PortfolioImagesPojo.DataBean>  = adapter?.mModelList!!
        mModelList.reverse()
        mModelList.add(Obj)
        mModelList.reverse()

        adapter?.mModelList = mModelList

        adapter?.notifyDataSetChanged()

        bindingObj.emptyLayout.visibility  = View.GONE
    }

    fun likeOrDislike(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int,likeBtn : ImageView)
    {
        if(activity?.isNetworkActiveWithMessage()!!)
        {
            likeBtn.isEnabled = false
            mLikesPresenter.like(user_id , profile_id ,status ,  portfolio_id ,"is_like" , position ,likeBtn)
        }
    }

    fun heartOrDisheart(user_id:String ,profile_id:String , status:String, portfolio_id:String ,position:Int ,heartBtn : ImageView)
    {
        if(activity?.isNetworkActiveWithMessage()!!)
        {
            heartBtn.isEnabled = false
            mLikesPresenter.heart(user_id , profile_id ,status ,  portfolio_id ,"is_heart" , position , heartBtn)
        }
    }

    override fun onLikeOrDislike(status: String, position: Int,likeBtn : ImageView)
    {
        likeBtn.isEnabled = true
        if(status.equals("1"))
        {
            adapter?.mModelList?.get(position)?.likes= ((adapter?.mModelList?.get(position)?.likes)!!.toInt() + 1).toString()
        }
        else
            adapter?.mModelList?.get(position)?.likes= ((adapter?.mModelList?.get(position)?.likes)!!.toInt() - 1).toString()

        adapter?.mModelList?.get(position)?.is_like = status.toInt()
        adapter?.notifyDataSetChanged()
    }

    override fun onHeartOrDisheart(status: String, position: Int ,heartBtn : ImageView)
    {
        heartBtn.isEnabled = true
        if(status.equals("1"))
        {
            adapter?.mModelList?.get(position)?.hearts= ((adapter?.mModelList?.get(position)?.hearts)!!.toInt() + 1).toString()
        }
        else
            adapter?.mModelList?.get(position)?.hearts= ((adapter?.mModelList?.get(position)?.hearts)!!.toInt() - 1).toString()

        adapter?.mModelList?.get(position)?.is_heart = status.toInt()
        adapter?.notifyDataSetChanged()
    }


    override fun onFailed(message: String)
    {
        isLoading =false
        showSnackbar(message)
    }

    override fun showProgress(visiblity: Boolean) {

        if(currentPage==PAGE_START && activity!=null) {
            if (visiblity) {
                (activity as PortfolioActivity).startProgressBarAnim()
            } else {
                (activity as PortfolioActivity).stopProgressBarAnim()
            }
        }
    }

    override fun showProgress(visiblity: Int) {

        if(activity!=null) {
            if (visiblity==1) {
                (activity as PortfolioActivity).startProgressBarAnim()
            } else {
                (activity as PortfolioActivity).stopProgressBarAnim()
            }
        }
    }



    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = (activity as Activity).findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        when (requestCode)
        {

            1000->
            {
                currentTotalListCount = 0
                Timber.e("Size:" +adapter?.mModelList?.size)
                adapter?.mModelList?.clear()
                Timber.e("Size:" +adapter?.mModelList?.size)
                adapter?.notifyDataSetChanged()
                currentPage = PAGE_START
                apiCalling((activity as PortfolioActivity).user_id  , currentPage.toString())
            }

            FilePickerConst.REQUEST_CODE_PHOTO -> if (resultCode == Activity.RESULT_OK && data != null)
            {
                val photoPaths: ArrayList<String> = ArrayList()
                photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA))

                if(photoPaths.size!=0)
                {
                    CropImage.activity(Uri.fromFile(File(photoPaths.get(0))))
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(2, 3)
                            .setCropShape(CropImageView.CropShape.RECTANGLE)
                            .setFixAspectRatio(true)
                            .setAllowRotation(false)
                            .setAllowCounterRotation(false)
                            .setAllowFlipping(false)
                            .setAutoZoomEnabled(true)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setCropMenuCropButtonTitle("Submit")
                            .start(context!!, this)
                }
                else
                {
                    showSnackbar("Error! Photo may be corrupted or not supported.")
                }

            }

            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE->
            {
                val result = CropImage.getActivityResult(data)

                if (resultCode == Activity.RESULT_OK)
                {
                    mPresenter.uploadImage((activity as PortfolioActivity).user_id , File(result.uri.path))

                }

                else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
                {
                    Toast.makeText(activity, "Failed to crop image. Please try again later: " + result.error, Toast.LENGTH_LONG).show()
                }

            }

        }
        // addThemToView(photoPaths, docPaths)

    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.cancelRetrofitRequest()
    }

}// Required empty public constructor
