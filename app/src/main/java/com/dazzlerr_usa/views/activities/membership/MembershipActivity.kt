package com.dazzlerr_usa.views.activities.membership

import android.app.Activity
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.braintreepayments.api.dropin.DropInActivity
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInResult

import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityMembershipBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.ccavenueutility.AvenuesParams
import com.dazzlerr_usa.utilities.ccavenueutility.ServiceUtility
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.views.activities.registersuccess.RegisterSuccessActivity
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class MembershipActivity : AppCompatActivity() , View.OnClickListener  , MembershipView , MembershipSuccessView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var bindingObj  : ActivityMembershipBinding
    lateinit var mPresenter: MembershipPresenter
    lateinit var mMembershipPresenter: MembershipSuccessPresenter
    var mMembershipPlanList:MutableList<MembershipPojo.Result> = ArrayList()
    var order_id=  ""
    var total_amount = ""
    var membership_id = ""
    var membership_plan_type = ""
    var membership_type = ""
    var apiSuccess = false
    var isPaymentSuccess =false
    var discount = ""
    var discount_type = ""
    var isDowngradeEnabled = ""
    var CurrentCurrency = ""
    var payment_mode = ""
    var BRAINTREE_REQUEST_CODE = 1003

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj =  DataBindingUtil.setContentView(this , R.layout.activity_membership )

        initializations()
        listeners()
        apiCalling()
    }

    fun initializations()
    {
        //--------Injecting the activity to dagger component-----
        (application as MyApplication).myComponent.inject(this)

        membership_type = intent?.extras?.getString("membership_type")!!

        if(intent?.extras?.containsKey("isDowngradeEnabled")!!)
            isDowngradeEnabled = intent?.extras?.getString("isDowngradeEnabled")!!

        CurrentCurrency = if(Constants.CURRENT_CURRENCY.isEmpty() || Constants.CURRENT_CURRENCY.equals("inr",ignoreCase = true))
        {
            Constants.RupeesSign
        } else
        {
            Constants.DollorSign
        }

        bindingObj.membershipMainLayout.visibility = View.GONE
        bindingObj.membershipProceedlayout.visibility = View.GONE
        bindingObj.titleLayout.titletxt.text = "Choose Membership"
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        mPresenter = MembershipModel(this, this)
        mMembershipPresenter = MembershipSuccessModel(this, this)
        bindingObj.membershipItemRegularPriceTxt.setPaintFlags(bindingObj.membershipItemRegularPriceTxt3.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        bindingObj.membershipItemRegularPriceTxt2.setPaintFlags(bindingObj.membershipItemRegularPriceTxt3.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        bindingObj.membershipItemRegularPriceTxt3.setPaintFlags(bindingObj.membershipItemRegularPriceTxt3.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

        if(membership_type.equals("register",ignoreCase = true))
        {
            bindingObj.membershipCurrentPlanLayout.visibility = View.GONE
            bindingObj.membershipFreePlanMainLayout.visibility = View.VISIBLE
        }
        else
        {
            bindingObj.membershipCurrentPlanLayout.visibility = View.VISIBLE
            bindingObj.membershipFreePlanMainLayout.visibility = View.GONE
        }
    }

    fun listeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.membershipBasicPlanLayout.setOnClickListener(this)
        bindingObj.membershipPremiumPlanLayout.setOnClickListener(this)
        bindingObj.membershipElitePlanLayout.setOnClickListener(this)
        bindingObj.membershipProceedBtn.setOnClickListener(this)
        bindingObj.membershipFreePlanLayout.setOnClickListener(this)
    }

    private fun apiCalling()
    {

        if(isNetworkActive()!!)
        {
            if(membership_type.equals("register",ignoreCase = true))
                mPresenter.getMembershipDetails("")
            else
                mPresenter.getMembershipDetails(sharedPreferences.getString(Constants.User_id).toString())
        }

        else
        {
            val dialog = CustomDialog(this)
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

    private fun checkoutApiCalling()
    {
        if(isNetworkActive()!!)
        {
            mMembershipPresenter.checkout(sharedPreferences.getString(Constants.User_id).toString()
                    , membership_id
                    , order_id.toString()
                    , discount , total_amount , ""
                    , payment_mode , "completed"
                    , membership_type  , ""
                    , sharedPreferences.getString(Constants.User_Address).toString()
                    , sharedPreferences.getString(Constants.billing_country).toString()
                    , sharedPreferences.getString(Constants.billing_city).toString()
                    , sharedPreferences.getString(Constants.billing_state).toString()
                    , sharedPreferences.getString(Constants.User_Zipcode).toString()
            )
        }

        else
        {
            val dialog = CustomDialog(this)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                checkoutApiCalling()
            })
            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener{
                dialog.dismiss()
            })
            dialog.show()
        }
    }


    override fun showProgress(visiblity: Boolean) {

        if(visiblity)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }
    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun setTitle(title: String)
    {
        bindingObj.titleLayout.titletxt.text = title
    }


    override fun onSuccess(model: MembershipPojo)
    {

        mMembershipPlanList = model.result


        if(mMembershipPlanList.size>=3) {
            if (model.result[0].sale_price.isEmpty()) {
                model.result[0].sale_price = model.result[0].regular_price
            }

            if (model.result[1].sale_price.isEmpty()) {
                model.result[1].sale_price = model.result[1].regular_price
            }

            if (model.result[2].sale_price.isEmpty()) {
                model.result[2].sale_price = model.result[2].regular_price
            }

            if (model.result[0].regular_price.isEmpty())
            {
                bindingObj.membershipItemRegularPrice.visibility = View.GONE
                bindingObj.membershipItemRegularPriceTxt.visibility = View.GONE
            }

            if (model.result[1].regular_price.isEmpty())
            {
                bindingObj.membershipItemRegularPrice2.visibility = View.GONE
                bindingObj.membershipItemRegularPriceTxt2.visibility = View.GONE
            }

            if (model.result[2].regular_price.isEmpty())
            {
                bindingObj.membershipItemRegularPrice3.visibility = View.GONE
                bindingObj.membershipItemRegularPriceTxt3.visibility = View.GONE
            }


        }


        discount = model.result[0].discount
        bindingObj.membershipBasicPlanLayout.setBackgroundResource(R.drawable.edittext_border)
        bindingObj.membershipItemPlanTxt.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPriceTxt.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPrice.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRadioBtn.setImageResource(R.drawable.grey_border_circle)

        bindingObj.membershipPremiumPlanLayout.setBackgroundResource(R.drawable.edittext_border)
        bindingObj.membershipItemPlanTxt2.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPriceTxt2.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPrice2.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRadioBtn2.setImageResource(R.drawable.grey_border_circle)

        bindingObj.membershipElitePlanLayout.setBackgroundResource(R.drawable.edittext_border)
        bindingObj.membershipItemPlanTxt3.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPriceTxt3.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRegularPrice3.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRadioBtn3.setImageResource(R.drawable.grey_border_circle)

        //Free membership Selector
        bindingObj.membershipFreePlanLayout.setBackgroundResource(R.drawable.edittext_border)
        bindingObj.membershipItemPlanTxt0.setTextColor(resources.getColor(R.color.colorBlack))
        bindingObj.membershipItemRadioBtn0.setImageResource(R.drawable.grey_border_circle)

        if(mMembershipPlanList.size>=3)
        {
            bindingObj.membershipItemPlanTxt.text = mMembershipPlanList.get(0).post_title.capitalize()+" Plan"
            bindingObj.membershipItemRegularPriceTxt.text = CurrentCurrency+mMembershipPlanList.get(0).regular_price
            bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(0).post_title.capitalize()+" Plan"
            //bindingObj.membershipPlanTxt.text = Constants.RupeesSign+mMembershipPlanList.get(0).sale_price+"/Year"
            bindingObj.membershipItemPlanRateTxt.text = CurrentCurrency+mMembershipPlanList.get(0).sale_price

            bindingObj.membershipItemPlanTxt2.text = mMembershipPlanList.get(1).post_title.capitalize()+" Plan"
            bindingObj.membershipItemRegularPriceTxt2.text = CurrentCurrency+mMembershipPlanList.get(1).regular_price
            bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(1).post_title.capitalize()+" Plan"
            //bindingObj.membershipPlanTxt.text = Constants.RupeesSign+mMembershipPlanList.get(1).sale_price+"/Year"
            bindingObj.membershipItemPlanRateTxt2.text = CurrentCurrency+mMembershipPlanList.get(1).sale_price

            bindingObj.membershipItemPlanTxt3.text = mMembershipPlanList.get(2).post_title.capitalize()+" Plan"
            bindingObj.membershipItemRegularPriceTxt3.text = CurrentCurrency+mMembershipPlanList.get(2).regular_price
            bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(2).post_title.capitalize()+" Plan"
            bindingObj.membershipPlanTxt.text = CurrentCurrency+mMembershipPlanList.get(2).sale_price+"/Year"
            bindingObj.membershipItemPlanRateTxt3.text = CurrentCurrency+mMembershipPlanList.get(2).sale_price
        }

        Timber.e("Membership id: "+ sharedPreferences.getString(Constants.Membership_id))
        if(sharedPreferences.getString(Constants.Membership_id).equals("0"))
        {
            //Hiding current plan for free member
            bindingObj.membershipCurrentPlanLayout.visibility = View.GONE

            planSelector(2)
        }

        else if(sharedPreferences.getString(Constants.Membership_id).equals("1"))
        {
            /*//Downgrading is only available from membership screen
            if(isDowngradeEnabled.isEmpty())
                bindingObj.membershipBasicPlanLayout.isEnabled = false*/

            if(isDowngradeEnabled.equals("false",ignoreCase = true)
                    && membership_type.equals("upgrade",ignoreCase = true))
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.GONE
            }
            else
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.VISIBLE
            }


            planSelector(2)

            bindingObj.membershipCurrentPlanTxt.text = mMembershipPlanList.get(0).post_title.capitalize()+" Plan"


        }

        else if(sharedPreferences.getString(Constants.Membership_id).equals("2"))
        {
           //Downgrading is only available from membership screen

            if(isDowngradeEnabled.equals("false",ignoreCase = true)
                    && membership_type.equals("upgrade",ignoreCase = true))
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.GONE
                bindingObj.membershipPremiumPlanMainLayout.visibility = View.GONE
            }
            else
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.VISIBLE
                bindingObj.membershipPremiumPlanMainLayout.visibility = View.VISIBLE
            }


            planSelector(3)

            bindingObj.membershipCurrentPlanTxt.text = mMembershipPlanList.get(1).post_title.capitalize()+" Plan"

            }

        else if(sharedPreferences.getString(Constants.Membership_id).equals("3"))
        {

            if(isDowngradeEnabled.equals("false",ignoreCase = true)
                    && membership_type.equals("upgrade",ignoreCase = true))
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.GONE
                bindingObj.membershipPremiumPlanMainLayout.visibility = View.GONE
            }
            else
            {
                bindingObj.membershipBasicPlanMainLayout.visibility = View.VISIBLE
                bindingObj.membershipPremiumPlanMainLayout.visibility = View.VISIBLE
            }

            planSelector(3)

            bindingObj.membershipCurrentPlanTxt.text = mMembershipPlanList.get(2).post_title.capitalize()+" Plan"

        }

        else
        {
            bindingObj.membershipBasicPlanLayout.isEnabled = true
            bindingObj.membershipPremiumPlanLayout.isEnabled = true
            bindingObj.membershipElitePlanLayout.isEnabled = true
        }

        bindingObj.membershipMainLayout.visibility = View.VISIBLE
        bindingObj.membershipProceedlayout.visibility = View.VISIBLE
        bindingObj.membershipPlanRecycler.layoutManager = LinearLayoutManager(this@MembershipActivity)
        bindingObj.membershipPlanRecycler.adapter = MembershipPlansAdapter(this , model.features)

    }

    override fun onSuccess(transaction_id : String)
    {

        apiSuccess =true
        val bundle = Bundle()

        bundle.putString("membership_type" , membership_type)
        bundle.putString("membership_id" , membership_id)
        bundle.putString("membership_plan_type" , membership_plan_type)
        bundle.putString("order_id" , transaction_id)
        bundle.putString("total_amount" , total_amount)
        bundle.putString("payment_mode" , payment_mode)

        sharedPreferences.saveString(Constants.Account_type , membership_plan_type.capitalize())
        sharedPreferences.saveString(Constants.Membership_id , membership_id)

        startActivity(Intent(this@MembershipActivity , MembershipSuccessActivity::class.java).putExtras(bundle))
        finish()

    }

    override fun onFreeMembershipSuccess() {

        apiSuccess =true
        val bundle = Bundle()

        sharedPreferences.saveString(Constants.Account_type , membership_plan_type.capitalize())
        sharedPreferences.saveString(Constants.Membership_id , membership_id)

        startActivity(Intent(this@MembershipActivity , RegisterSuccessActivity::class.java))
        finish()
    }

    override fun onGetToken(model: GetTokenPojo) {

        var dropInRequest = DropInRequest().clientToken(model.client_token);
        startActivityForResult(dropInRequest.getIntent(this),BRAINTREE_REQUEST_CODE )
    }
    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun onMembershipFailed(message: String)
    {
        val customDialog  =CustomDialog(this@MembershipActivity)
        customDialog.setTitle("Alert!")
        customDialog.setMessage(message)
        customDialog.setPositiveButton("Try again") {
            customDialog.dismiss()
            checkoutApiCalling()
        }
        customDialog.show()
    }

    override fun onFreeMembershipFailed(message: String) {
        showSnackbar(message)
    }

    fun showSnackbar(message: String)
    {
        try
        {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun planSelector(type: Int) //1 for basic plan, 2 for premium plan and 3 for elite plan
    {

        if(type==1)//Basic Plan Selected
        {

            bindingObj.membershipBasicPlanLayout.setBackgroundResource(R.drawable.edittext_border_appcolor)
            bindingObj.membershipItemPlanTxt.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPriceTxt.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPrice.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRadioBtn.setImageResource(R.drawable.appcolor_border_circle)

            bindingObj.membershipPremiumPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn2.setImageResource(R.drawable.grey_border_circle)

            bindingObj.membershipElitePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn3.setImageResource(R.drawable.grey_border_circle)

            //Free membership Selector
            bindingObj.membershipFreePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt0.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn0.setImageResource(R.drawable.grey_border_circle)

            if(mMembershipPlanList.size>=3)
            {
                bindingObj.membershipItemPlanTxt.text = mMembershipPlanList.get(0).post_title.capitalize()+" Plan"
                bindingObj.membershipItemRegularPriceTxt.text = CurrentCurrency+mMembershipPlanList.get(0).regular_price
                bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(0).post_title.capitalize()+" Plan"
                bindingObj.membershipPlanTxt.text = CurrentCurrency+mMembershipPlanList.get(0).sale_price+"/Year"
                bindingObj.membershipItemPlanRateTxt.text = CurrentCurrency+mMembershipPlanList.get(0).sale_price
                membership_id = mMembershipPlanList.get(0).membership_id
                total_amount = mMembershipPlanList.get(0).sale_price
                membership_plan_type = mMembershipPlanList.get(0).post_title

            }

        }

        else if(type==2)//Premium Plan Selected
        {
            bindingObj.membershipPremiumPlanLayout.setBackgroundResource(R.drawable.edittext_border_appcolor)
            bindingObj.membershipItemPlanTxt2.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPriceTxt2.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPrice2.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRadioBtn2.setImageResource(R.drawable.appcolor_border_circle)

            bindingObj.membershipBasicPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn.setImageResource(R.drawable.grey_border_circle)

            bindingObj.membershipElitePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn3.setImageResource(R.drawable.grey_border_circle)

            //Free membership Selector
            bindingObj.membershipFreePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt0.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn0.setImageResource(R.drawable.grey_border_circle)

            if(mMembershipPlanList.size>=3)
            {
                bindingObj.membershipItemPlanTxt2.text = mMembershipPlanList.get(1).post_title.capitalize()+" Plan"
                bindingObj.membershipItemRegularPriceTxt2.text = CurrentCurrency+mMembershipPlanList.get(1).regular_price
                bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(1).post_title.capitalize()+" Plan"
                bindingObj.membershipPlanTxt.text = CurrentCurrency+mMembershipPlanList.get(1).sale_price+"/Year"
                bindingObj.membershipItemPlanRateTxt2.text = CurrentCurrency+mMembershipPlanList.get(1).sale_price

                membership_id = mMembershipPlanList.get(1).membership_id
                total_amount = mMembershipPlanList.get(1).sale_price
                membership_plan_type = mMembershipPlanList.get(1).post_title
            }
        }

        else if(type==3)//Premium Plan Selected
        {

            bindingObj.membershipElitePlanLayout.setBackgroundResource(R.drawable.edittext_border_appcolor)
            bindingObj.membershipItemPlanTxt3.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPriceTxt3.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRegularPrice3.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRadioBtn3.setImageResource(R.drawable.appcolor_border_circle)

            bindingObj.membershipBasicPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn.setImageResource(R.drawable.grey_border_circle)

            bindingObj.membershipPremiumPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn2.setImageResource(R.drawable.grey_border_circle)

            //Free membership Selector
            bindingObj.membershipFreePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt0.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn0.setImageResource(R.drawable.grey_border_circle)

            if(mMembershipPlanList.size>=3)
            {
                bindingObj.membershipItemPlanTxt3.text = mMembershipPlanList.get(2).post_title.capitalize()+" Plan"
                bindingObj.membershipItemRegularPriceTxt3.text = CurrentCurrency+mMembershipPlanList.get(2).regular_price
                bindingObj.membershipPlanTypeTxt.text = mMembershipPlanList.get(2).post_title.capitalize()+" Plan"
                bindingObj.membershipPlanTxt.text = CurrentCurrency+mMembershipPlanList.get(2).sale_price+"/Year"
                bindingObj.membershipItemPlanRateTxt3.text = CurrentCurrency+mMembershipPlanList.get(2).sale_price

                membership_id = mMembershipPlanList.get(2).membership_id
                total_amount = mMembershipPlanList.get(2).sale_price
                membership_plan_type = mMembershipPlanList.get(2).post_title
            }
        }


        else if(type==4)//Free Plan Selected
        {

            bindingObj.membershipFreePlanLayout.setBackgroundResource(R.drawable.edittext_border_appcolor)
            bindingObj.membershipItemPlanTxt0.setTextColor(resources.getColor(R.color.appColor))
            bindingObj.membershipItemRadioBtn0.setImageResource(R.drawable.appcolor_border_circle)

            bindingObj.membershipBasicPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn.setImageResource(R.drawable.grey_border_circle)

            bindingObj.membershipPremiumPlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice2.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn2.setImageResource(R.drawable.grey_border_circle)

            bindingObj.membershipElitePlanLayout.setBackgroundResource(R.drawable.edittext_border)
            bindingObj.membershipItemPlanTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPriceTxt3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRegularPrice3.setTextColor(resources.getColor(R.color.colorBlack))
            bindingObj.membershipItemRadioBtn3.setImageResource(R.drawable.grey_border_circle)

            if(mMembershipPlanList.size>=3)
            {

                bindingObj.membershipPlanTypeTxt.text = "Free"
                bindingObj.membershipPlanTxt.text = ""

                membership_id = "0"
                total_amount = "0"
                membership_plan_type = "Free"
            }
        }
    }

    override fun onClick(v: View?) {

        when(v?.id) {
            R.id.leftbtn -> {
                finish()
            }

            R.id.membershipBasicPlanLayout -> {
                planSelector(1)//Basic Plan
            }

            R.id.membershipPremiumPlanLayout -> {
                planSelector(2)//Premium Plan
            }

            R.id.membershipElitePlanLayout -> {
                planSelector(3)//Elite Plan
            }
            R.id.membershipFreePlanLayout -> {
                planSelector(4)//Free Plan
            }

            R.id.membershipProceedBtn ->
            {

                if(membership_id.equals("0"))
                {
                 if(isNetworkActiveWithMessage())
                 {
                     mMembershipPresenter.buyFreeMembership(sharedPreferences.getString(Constants.User_id).toString())
                 }
                }
                else
                {
                    isPaymentSuccess = true
                    val intent = Intent(this@MembershipActivity, MemershipAddressActivity::class.java)
                    intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(order_id.toString()).toString().trim { it <= ' ' })
                    startActivityForResult(intent, 1002)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode)
        {
            1002->
            {

                 if( data?.extras!=null)
                {
                  var bundle: Bundle = data?.extras!!

                    Timber.e("Payment_mode: "+ bundle.getString("payment_mode") )
                    //payment_mode = bundle.getString("payment_mode").toString()
                    payment_mode = "Paypal"

                    getClientTokenFromServer()
                    //checkoutApiCalling()
            }
            }


            BRAINTREE_REQUEST_CODE->
            {
                if (RESULT_OK == resultCode)
                {
                    var result: DropInResult = data!!.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT)

                    var paymentNonce = result.getPaymentMethodNonce()!!.getNonce()

                    paymentNonce.get(0)
                    //send to your server
                    Log.e("Success", paymentNonce)
                    order_id= paymentNonce
                    checkoutApiCalling()

                }else if(resultCode == Activity.RESULT_CANCELED){
                    Log.d("Error", "User cancelled payment");
                }else {
                    var  error = data?.getSerializableExtra(DropInActivity.EXTRA_ERROR) as java.lang.Exception
                    Log.d("Error", ""+error)
                }
            }
        }
    }



    private fun getClientTokenFromServer()
    {

        if(isNetworkActiveWithMessage())
        {
            mPresenter.getTokenApi()
        }
    }

    override fun onDestroy()
    {
        super.onDestroy()

        if(!apiSuccess && !isPaymentSuccess && membership_type.equals("register",ignoreCase = true))
        {
            sharedPreferences.clear()
        }
    }
}




