package com.dazzlerr_usa.views.activities.elitecontact

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEliteContactBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import javax.inject.Inject

class EliteContactActivity : AppCompatActivity(),View.OnClickListener  , EliteContactView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences
    lateinit var mPresenter: EliteContactPresenter
    lateinit var bindingObj: ActivityEliteContactBinding
    var type= ""
    var profile_name = ""
    var user_name = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_elite_contact)
        initializations()
        clickListeners()
    }

    fun initializations()
    {

        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)

        type = intent.extras!!.getString("type","")
        profile_name = intent.extras!!.getString("profile_name","")
        user_name = intent.extras!!.getString("username","")

        mPresenter = EliteContactModel(this , this)
        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Contact"
        bindingObj.contactNote.visibility = View.VISIBLE

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

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.sendMessageBtn.setOnClickListener(this)
    }

    private fun apiCalling(name:String ,phone:String,email:String,msg:String,type:String,profile_name:String,user_name:String)
    {

        if(this@EliteContactActivity?.isNetworkActive()!!)
        {
            mPresenter?.isValid(name ,phone ,email , msg , type , profile_name)
        }

        else
        {

            val dialog  = CustomDialog(this@EliteContactActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(name ,phone ,email , msg , type , profile_name , user_name)
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
                ,bindingObj.contactPhone.text.toString().trim()
                ,bindingObj.contactEmail.text.toString().trim()
                ,bindingObj.contactMessage.text.toString().trim()
                ,type
                ,profile_name
                ,user_name)
    }

    override fun onSuccess()
    {
        Toast.makeText(this , "Contact Request has been Submitted" , Toast.LENGTH_LONG).show()
        finish()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
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
                    ,bindingObj.contactPhone.text.toString().trim()
                    ,bindingObj.contactEmail.text.toString().trim()
                    ,bindingObj.contactMessage.text.toString().trim()
                    ,type
                    ,profile_name,user_name
            )
            }

        }
    }


}
