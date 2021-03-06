package com.dazzlerr_usa.views.activities.contactus

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityProfileContactBinding
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.google.android.gms.ads.AdRequest
import javax.inject.Inject

class ContactUsActivity : AppCompatActivity() , View.OnClickListener , ContactUsView {


    lateinit var mPresenter: ContactUsPresenter
    lateinit var bindingObj: ActivityProfileContactBinding

    @Inject
    lateinit var sharedPreferences : HelperSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_profile_contact)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        showAds()

        mPresenter = ContactUsModel(this , this)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Contact Us"

        if(!sharedPreferences.getString(Constants.User_id).equals(""))
        {
            bindingObj.contactName.setText(sharedPreferences.getString(Constants.User_name).toString())
            bindingObj.contactEmail.setText(sharedPreferences.getString(Constants.User_Email).toString())
            bindingObj.contactPhone.setText(sharedPreferences.getString(Constants.User_Phone).toString())
        }

        bindingObj.contactMessage?.addTextChangedListener(object : TextWatcher
        {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int)
            {

            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int)
            {
                try
                {
                    bindingObj.characterLeftTxt?.text = "Characters left: " + (300 - s.length)
                }

                catch (e:Exception)
                {
                    e.printStackTrace()
                }
            }
        })

    }

    fun showAds()
    {
        //Initializing Google ads
/*
        val testDeviceIds: List<String> = Arrays.asList("A86FED701CDC6E77D341DEBA488306E3")
        val configuration: RequestConfiguration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
        MobileAds.setRequestConfiguration(configuration)
*/

        //MobileAds.initialize(this) {}

        if(sharedPreferences.getString(Constants.Membership_id).equals("")
                ||sharedPreferences.getString(Constants.Membership_id).equals("0")
                ||sharedPreferences.getString(Constants.Membership_id).equals("1")
                ||sharedPreferences.getString(Constants.Membership_id).equals("6")
        )
        {
            val adRequest = AdRequest.Builder().build()
            bindingObj.adView.visibility = View.VISIBLE
            bindingObj.adView.loadAd(adRequest)
            //--------------------------
        }

    }
    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.sendMessageBtn.setOnClickListener(this)
    }

    private fun apiCalling(name:String ,email:String ,phone:String ,subject:String ,message:String)
    {

        if(this@ContactUsActivity.isNetworkActive())
        {
            mPresenter.isValid(name  ,email  , phone , subject , message)
        }

        else
        {
            val dialog = CustomDialog(this@ContactUsActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(name  ,email  , phone , subject , message)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onValidate()
    {
        mPresenter.contact(bindingObj.contactName.text.toString().trim()
                ,bindingObj.contactEmail.text.toString().trim()
                ,bindingObj.contactPhone.text.toString().trim()
                ,bindingObj.contactSubject.text.toString().trim()
                ,bindingObj.contactMessage.text.toString().trim())
    }

    override fun onSuccess()
    {
        Toast.makeText(this , "Message has been sent" , Toast.LENGTH_LONG).show()
        finish()
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

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
            {
                finish()
            }
            R.id.sendMessageBtn->
            {
                apiCalling(bindingObj.contactName.text.toString().trim()
                        ,bindingObj.contactEmail.text.toString().trim()
                        ,bindingObj.contactPhone.text.toString().trim()
                        ,bindingObj.contactSubject.text.toString().trim()
                        ,bindingObj.contactMessage.text.toString().trim())
            }

        }
    }

}

