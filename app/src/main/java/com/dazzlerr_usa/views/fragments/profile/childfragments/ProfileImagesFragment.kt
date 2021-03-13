package com.dazzlerr_usa.views.fragments.profile.childfragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.FragmentProfileBinding
import com.dazzlerr_usa.databinding.FragmentProfileImagesBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.GridSpacingItemDecoration
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.imageslider.ImageSliderModel
import com.dazzlerr_usa.utilities.imageslider.ImageSliderPresenter
import com.dazzlerr_usa.utilities.imageslider.ImageSliderView
import com.dazzlerr_usa.views.activities.portfolio.PortfolioActivity
import com.dazzlerr_usa.views.fragments.portfolio.models.PortfolioImagesModel
import com.dazzlerr_usa.views.fragments.portfolio.pojos.PortfolioImagesPojo
import com.dazzlerr_usa.views.fragments.portfolio.pojos.UploadImagePojo
import com.dazzlerr_usa.views.fragments.portfolio.presenters.PortfolioImagesPresenter
import com.dazzlerr_usa.views.fragments.portfolio.views.PortfolioImagesView
import com.dazzlerr_usa.views.fragments.profile.adapters.ProfileImagesAdapter
import java.util.ArrayList
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class ProfileImagesFragment : Fragment() ,   PortfolioImagesView , View.OnClickListener, ImageSliderView {

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mLikesPresenter: ImageSliderPresenter
    lateinit var bindingObj: FragmentProfileImagesBinding
    lateinit var mPresenter: PortfolioImagesPresenter
    lateinit var mAdapter: ProfileImagesAdapter
    lateinit var profile_id: String
    var portfolioList: MutableList<PortfolioImagesPojo.DataBean>? = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        bindingObj = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_images, container, false)
        initializations()
        clickListeners()

        return bindingObj.root
    }

    fun initializations() {
        ((activity?.application) as MyApplication).myComponent.inject(this)
        mPresenter = PortfolioImagesModel(activity as Activity, this)
        mLikesPresenter = ImageSliderModel(activity as Activity , this)
        mAdapter = ProfileImagesAdapter(activity as Activity ,portfolioList!! , sharedPreferences.getString(Constants.User_id).toString() ,this@ProfileImagesFragment)
        bindingObj.profileImagesRecyclerView.layoutManager = GridLayoutManager(activity, 2)
        val spanCount = 2 // 3 columns
        val spacing = 2 // 4px
        val includeEdge = true
        bindingObj.profileImagesRecyclerView.addItemDecoration(GridSpacingItemDecoration(spanCount, spacing, includeEdge))
        bindingObj.profileImagesRecyclerView.adapter = mAdapter

        profile_id = arguments?.getString("profile_id").toString()
        mPresenter?.getPortfolioImages(profile_id, sharedPreferences.getString(Constants.User_id).toString(), "1", "")
    }

    fun clickListeners() {
        bindingObj.profileImagesExploreBtn.setOnClickListener(this)
    }

    override fun onSuccess(model: PortfolioImagesPojo) {

        if (model.data?.size != 0)
        {
            bindingObj.emptyLayout.visibility = View.GONE
        portfolioList?.clear()
        portfolioList?.addAll(model?.data!!)
        mAdapter.notifyDataSetChanged()
        }
        else
        {

            bindingObj.emptyLayout.visibility = View.VISIBLE
        }
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
    override fun onLikeOrDislike(status: String, position: Int ,likeBtn : ImageView)
    {
        likeBtn.isEnabled = true
        if(status.equals("1"))
        {
            portfolioList?.get(position)?.likes= ((portfolioList?.get(position)?.likes)!!.toInt() + 1).toString()
        }
        else
            portfolioList?.get(position)?.likes= ((portfolioList?.get(position)?.likes)!!.toInt() - 1).toString()

        portfolioList?.get(position)?.is_like = status.toInt()
        mAdapter?.notifyDataSetChanged()
    }

    override fun onHeartOrDisheart(status: String, position: Int , heartBtn: ImageView)
    {
        heartBtn.isEnabled = true

        if(status.equals("1"))
        {
            portfolioList?.get(position)?.hearts= ((portfolioList?.get(position)?.hearts)!!.toInt() + 1).toString()
        }
        else
            portfolioList?.get(position)?.hearts= ((portfolioList?.get(position)?.hearts)!!.toInt() - 1).toString()

        portfolioList?.get(position)?.is_heart = status.toInt()
        mAdapter?.notifyDataSetChanged()

    }


    override fun onImageDelete(selectedItemsToDelete: ArrayList<String>) {

    }

    override fun onImageUpload(model: UploadImagePojo) {

    }

    override fun onFailed(message: String) {

    }

    override fun showProgress(visiblity: Boolean) {

    }

    override fun showProgress(visiblity: Int) {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.profileImagesExploreBtn -> {
                val bundle = Bundle()
                bundle.putString("from", arguments?.getString("from"))
                bundle.putString("user_id", arguments?.getString("profile_id"))
                bundle.putString("shouldShowProjects", "false")
                bundle.putString("isProfileEdit", "false")
                val intent = Intent(activity, PortfolioActivity::class.java)
                intent.putExtras(bundle)
                startActivity(intent)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {

            1000 ->
            {
                mPresenter?.getPortfolioImages(profile_id, sharedPreferences.getString(Constants.User_id).toString(), "1" ,"")

            }

        }

    }
}