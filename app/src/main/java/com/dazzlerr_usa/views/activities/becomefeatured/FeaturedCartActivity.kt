package com.dazzlerr_usa.views.activities.becomefeatured

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityFeaturedCartBinding
import com.dazzlerr_usa.utilities.ccavenueutility.Constants
import com.dazzlerr_usa.views.activities.featuredccavenue.AddressActivity
import java.lang.Exception

class FeaturedCartActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var bindingObj: ActivityFeaturedCartBinding
    var membership_id = ""
    var product_id = ""
    var valid_till = ""
    var order_total = ""
    var wp_user_id=  ""
    var bannerImgUrl = ""
    var user_id = ""
    var discount = 0
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_featured_cart)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        bindingObj.titleLayout.titletxt.text = "Cart"
        product_id = intent.extras!!.getString("product_id").toString()
        valid_till = intent.extras!!.getString("valid_till").toString()
        order_total = intent.extras!!.getString("order_total").toString()
        user_id = intent.extras!!.getString("user_id").toString()
        wp_user_id = intent.extras!!.getString("wp_user_id").toString()
        membership_id= intent.extras!!.getString("membership_id").toString()
        bannerImgUrl = intent.extras!!.getString("banner_image").toString()

        if(intent.extras!!.getString("discount").isNullOrEmpty())
        {
            discount = 0
        }
        else
        {
            try {
                discount=intent.extras!!.getString("discount")!!.toInt()
            }
            catch(e: Exception)
            {
                e.printStackTrace()
                discount = 0
            }
        }

        if(discount==0)
            bindingObj.discountlayout.visibility = View.GONE
        else
            bindingObj.discountlayout.visibility = View.VISIBLE

        Glide.with(this@FeaturedCartActivity)
                .load("https://www.dazzlerr.com/API/"+bannerImgUrl)
                .placeholder(R.drawable.model_placeholder)
                .thumbnail(.2f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(bindingObj.cartOfferImage)

        if(valid_till.equals("1"))
            bindingObj.cartMembershipValidityTxt.text = "Valid for "+valid_till+" month"

        else
            bindingObj.cartMembershipValidityTxt.text = "Valid for "+valid_till+" months"


        if(Constants.CURRENCY.equals("inr",ignoreCase = true))
        bindingObj.cartMembershipPriceTxt.text = com.dazzlerr_usa.utilities.Constants.RupeesSign+order_total+"/-"

        else
            bindingObj.cartMembershipPriceTxt.text = com.dazzlerr_usa.utilities.Constants.DollorSign+order_total+"/-"


        if(Constants.CURRENCY.equals("inr",ignoreCase = true))
        bindingObj.cartMembershipdetailPriceTxt.text = com.dazzlerr_usa.utilities.Constants.RupeesSign+order_total

        else
            bindingObj.cartMembershipdetailPriceTxt.text = com.dazzlerr_usa.utilities.Constants.DollorSign+order_total


        if(Constants.CURRENCY.equals("inr",ignoreCase = true))
        bindingObj.cartMembershipdetaildiscountTxt.text = "- "+com.dazzlerr_usa.utilities.Constants.RupeesSign+discount

        else
            bindingObj.cartMembershipdetaildiscountTxt.text = "- "+com.dazzlerr_usa.utilities.Constants.DollorSign+discount

        var total = 0

        if(order_total.isNullOrEmpty())
        {
            total = 0
        }
        else
            total = order_total.toInt()

        order_total = (total-discount).toString()

        if(Constants.CURRENCY.equals("inr",ignoreCase = true))
        bindingObj.cartMembershipdetailTotalPriceTxt.text = com.dazzlerr_usa.utilities.Constants.RupeesSign+order_total
       else
            bindingObj.cartMembershipdetailTotalPriceTxt.text = com.dazzlerr_usa.utilities.Constants.DollorSign+order_total

    }

    fun clickListeners()
    {
        bindingObj.cartPlaceOrderBtn.setOnClickListener(this)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
            {
                finish()
            }
            R.id.cartPlaceOrderBtn->
            {
                val bundle = Bundle()
                bundle.putString("product_id" ,product_id)
                bundle.putString("valid_till" , valid_till)
                bundle.putString("order_total" , order_total)
                bundle.putString("user_id" , user_id)
                bundle.putString("wp_user_id" , wp_user_id)
                bundle.putString("membership_id" , membership_id)
                bundle.putString("banner_image" , bannerImgUrl)

                startActivity(Intent(this@FeaturedCartActivity , AddressActivity::class.java).putExtras(bundle))

            }

        }
    }
}
