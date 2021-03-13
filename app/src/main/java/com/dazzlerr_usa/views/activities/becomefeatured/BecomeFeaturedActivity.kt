package com.dazzlerr_usa.views.activities.becomefeatured

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityBecomeFeaturedBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import javax.inject.Inject

class BecomeFeaturedActivity : AppCompatActivity() , View.OnClickListener, BecomeFeaturedView
{

    lateinit var bindingObj: ActivityBecomeFeaturedBinding
    var membership_id = ""
    var product_id = ""
    var valid_till = ""
    var order_total = ""
    var wp_user_id=  ""
    var bannerImgUrl = ""
    var discount = ""

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter: BecomeFeaturedPresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_become_featured)
        initializations()
        clickListeners()
        apiCalling()
    }

    fun initializations()
    {
        bindingObj.buyNowBtn.visibility = View.GONE
        (application as MyApplication).myComponent.inject(this@BecomeFeaturedActivity)
        mPresenter = BecomeFeaturedModel(this , this)
    }

    fun clickListeners()
    {
        bindingObj.buyNowBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }

    private fun apiCalling()
    {

        if(this@BecomeFeaturedActivity?.isNetworkActive()!!)
        {
            mPresenter.addWpUser(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {
            val dialog = CustomDialog(this@BecomeFeaturedActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling()
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener{
                dialog.dismiss()
            })
            dialog.show()
        }
    }


    override fun onSuccess(model: BecomeFeaturedPojo)
    {

        Glide.with(this@BecomeFeaturedActivity)
                .load("https://www.dazzlerr.com/API/"+model.membership_banner)
                .thumbnail(.2f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bindingObj.bannerImg)

        bindingObj.buyNowBtn.visibility = View.VISIBLE

        membership_id = model.membership_id.toString()
        product_id =  model.product_id.toString()
        valid_till =  model.valid_till.toString()
        order_total =  model.order_total.toString()
        wp_user_id=   model.wp_user_id.toString()
        bannerImgUrl = model.membership_banner.toString()
        discount = model.discount.toString()
    }

    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
    }
    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun startProgressBarAnim()
    {
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

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
            {
                finish()
            }
            R.id.buyNowBtn->
            {
                val bundle = Bundle()
                bundle.putString("product_id" ,product_id)
                bundle.putString("valid_till" , valid_till)
                bundle.putString("order_total" , order_total)
                bundle.putString("user_id" , sharedPreferences.getString(Constants.User_id))
                bundle.putString("wp_user_id" , wp_user_id)
                bundle.putString("membership_id" , membership_id)
                bundle.putString("banner_image" , bannerImgUrl)
                bundle.putString("discount" , discount)

                startActivity(Intent(this@BecomeFeaturedActivity , FeaturedCartActivity::class.java).putExtras(bundle))

            }

        }
    }
}
