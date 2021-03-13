package com.dazzlerr_usa.views.activities.elitememberdetails

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEliteMemberDetailsBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import javax.inject.Inject
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.mainactivity.MainActivity
import com.dazzlerr_usa.views.activities.elitecontact.EliteContactActivity


class EliteMemberDetailsActivity : AppCompatActivity() , View.OnClickListener , EliteMembersDetailsView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: EliteMembersDetailsPresenter
    lateinit var bindingObj : ActivityEliteMemberDetailsBinding
    lateinit var mPortfolioAdapter: ElitePortfolioAdapter
    var mPortfolioList: MutableList<EliteMemberDetailsPojo.PortfolioBean> = ArrayList()
    var profile_name = ""
    var user_name = ""
    var profile_id = ""
    var likeStatus = 0
    var likesCount = 0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_elite_member_details)
        initializations()
        clickListeners()
        bindingObj.mainLayout.visibility = View.GONE
        apiCalling(sharedPreferences.getString(Constants.User_id).toString() , intent.extras!!.getString("profile_id",""))
    }

    fun initializations()
    {
        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)
        mPresenter = EliteMembersDetailsModel(this , this)

    }

    fun clickListeners()
    {
        bindingObj.backBtn.setOnClickListener(this)
        bindingObj.contactBtn.setOnClickListener(this)
        bindingObj.likeBtn.setOnClickListener(this)
    }

    private fun apiCalling(user_id:String ,profile_id:String)
    {

        if(this@EliteMemberDetailsActivity?.isNetworkActive()!!)
        {
            mPresenter?.getEliteMemberDetails(user_id ,profile_id)
        }

        else
        {

            val dialog = CustomDialog(this@EliteMemberDetailsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(user_id ,profile_id)
                    })

                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onSuccess(model: EliteMemberDetailsPojo)
    {
        bindingObj.mainLayout.visibility = View.VISIBLE
        if (model.result?.size != 0)
        {
            bindingObj.binderObj = model.result?.get(0)
            bindingObj.executePendingBindings()

            profile_name = model.result?.get(0)?.name!!
            user_name = model.result?.get(0)?.username!!
            profile_id = model.result?.get(0)?.profile_id.toString()

            if(!model.result?.get(0)?.likes!!.equals("") && !model.result?.get(0)?.likes.equals("null"))
                likesCount  = model.result?.get(0)?.likes?.toInt()!!

            else
                likesCount = 0

            if(!model.result?.get(0)?.like_status!!.equals("") && !model.result?.get(0)?.like_status.equals("null"))
                likeStatus  = model.result?.get(0)?.like_status!!.toInt()

            else
                likeStatus = 0

            likeDislikeHandler()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bindingObj.aboutTxt.setText(Html.fromHtml(model.result?.get(0)?.about, Html.FROM_HTML_MODE_LEGACY));
            }

            else
            {
                bindingObj.aboutTxt.setText(Html.fromHtml(model.result?.get(0)?.about));
            }

            loadEliteImage(bindingObj.timelineImage , model.result?.get(0)?.desktop_banner.toString())
            loadEliteImage(bindingObj.portfolioImage , model.result?.get(0)?.gallery_banner.toString())

            mPortfolioList = model?.portfolio!!
            mPortfolioAdapter = ElitePortfolioAdapter(this@EliteMemberDetailsActivity , mPortfolioList)
            val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
            bindingObj.portfolioRecyclerView.setLayoutManager(staggeredGridLayoutManager)
            bindingObj.portfolioRecyclerView.adapter = mPortfolioAdapter
        }
    }

    override fun onLikeOrDislike(status: String) {

        likeStatus = status.toInt()

        if(likeStatus==1) {
            try {
                likesCount = (likesCount + 1)
                bindingObj.eliteLikesCountTxt.text = likesCount.toString()+" Likes"

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
        else
        {
            try {
                likesCount = (likesCount - 1)
                bindingObj.eliteLikesCountTxt.text = likesCount.toString()+" Likes"
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        likeDislikeHandler()
    }


    override fun onFailed(message: String)
    {
        showSnackbar(message)
        bindingObj.mainLayout.visibility = View.GONE
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }

    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.setVisibility(View.VISIBLE)
    }

    fun stopProgressBarAnim()
    {
        bindingObj.aviProgressBar.setVisibility(View.GONE)
    }

    fun showSnackbar(message: String)
    {
        try {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun likeDislikeHandler()
    {
        if(likeStatus==1)
        {
            bindingObj.likeBtnTxt.text = "Liked"
        }
        else
        {
            bindingObj.likeBtnTxt.text = "Like"
        }
    }
    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.backBtn-> finish()

            R.id.contactBtn->
            {
                if(profile_name.length!=0 && isUserLogined())
                {
                    val bundle = Bundle()
                    bundle.putString("profile_name", profile_name)
                    bundle.putString("username", user_name)
                    bundle.putString("type", "elite")
                    startActivity(Intent(this@EliteMemberDetailsActivity, EliteContactActivity::class.java).putExtras(bundle))
                }
            }

            R.id.likeBtn->
            {
                if(isUserLogined())
                {
                    if(likeStatus==1)
                    mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+0)

                    else if(likeStatus==0)
                        mPresenter.likeOrDislike(sharedPreferences.getString(Constants.User_id).toString() ,profile_id , ""+1)

                }
            }
    }
    }

    fun isUserLogined():Boolean
    {
        if(sharedPreferences.getString(Constants.User_id).equals(""))
        {
           val dialog  =  CustomDialog(this)
            dialog.setMessage(resources.getString(R.string.signin_txt))
            dialog.setPositiveButton("Continue", DialogListenerInterface.onPositiveClickListener {

                        val bundle = Bundle()
                        bundle.putString("ShouldOpenLogin"  , "true")
                        val newIntent = Intent(this@EliteMemberDetailsActivity, MainActivity::class.java)
                        newIntent.putExtras(bundle)
                        newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(newIntent)
                    })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
            dialog.show()

            return false
        }

        else
            return true
    }


}
