package com.dazzlerr_usa.views.activities.report

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityProfileReportBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import com.dazzlerr_usa.extensions.isNetworkActive
import com.google.android.flexbox.*
import javax.inject.Inject

class ProfileReportActivity : AppCompatActivity() ,View.OnClickListener , ReportView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var mPresenter: ReportPresenter
    lateinit var bindingObj: ActivityProfileReportBinding
    var profile_id = ""

    val mList: MutableList<String> = ArrayList()
    var reason = ""
    var subject = "Request for report"
    var isOtherReasonVisible = false

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_profile_report)
        initializations()
        clickListeners()
    }


    fun initializations()
    {
        //----------------Injecting dagger into sharedepreferences
        (application as MyApplication).myComponent.inject(this)

        mPresenter = ReportModel(this , this)

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = "Report"

        bindingObj.contactName.setText(sharedPreferences.getString(Constants.User_name).toString())
        bindingObj.contactEmail.setText(sharedPreferences.getString(Constants.User_Email).toString())
        bindingObj.contactPhone.setText(sharedPreferences.getString(Constants.User_Phone).toString())

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

        val reasonsLayoutManager = FlexboxLayoutManager(this@ProfileReportActivity)
        reasonsLayoutManager.flexDirection = FlexDirection.ROW
        reasonsLayoutManager.justifyContent = JustifyContent.FLEX_START
        //layoutManager.alignContent = AlignContent.STRETCH
        reasonsLayoutManager.alignItems= AlignItems.STRETCH
        reasonsLayoutManager.flexWrap = FlexWrap.WRAP
        bindingObj.reportReasonsRecyclerView.setLayoutManager(reasonsLayoutManager)

        if(intent.extras!!.getString("type","").equals("message",ignoreCase = true))
        {
            mList.add("Fake Profile")
            mList.add("Fake Bio")
            mList.add("Inappropriate message")
            mList.add("Spam")
            mList.add("Terrorism")
            mList.add("Something else")
        }

        else
        {
            mList.add("Nudity")
            mList.add("Violence")
            mList.add("Harassment")
            mList.add("Suicide or Self-Injury")
            mList.add("False news")
            mList.add("Spam")
            mList.add("Unauthorized Sales")
            mList.add("Hate Speech")
            mList.add("Terrorism")
            mList.add("Something else")
        }

        bindingObj.reportReasonsRecyclerView.adapter = ReportReasonsAdapter(this@ProfileReportActivity , mList)
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.sendMessageBtn.setOnClickListener(this)
    }

    private fun apiCalling(name:String ,email:String ,phone:String ,subject:String ,message:String)
    {

        if(this@ProfileReportActivity?.isNetworkActive()!!)
        {
            mPresenter?.isValid(name  ,email  , phone , subject , message)
        }

        else
        {
            val dialog = CustomDialog(this@ProfileReportActivity)
                    dialog.setTitle(resources.getString(R.string.no_internet_txt))
                    dialog.setMessage(resources.getString(R.string.connection_txt))
                    dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                        apiCalling(name  ,email  , phone , subject , message)
                    })
                    dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener{
                        dialog.dismiss()
                    })
                    dialog.show()
        }
    }

    override fun onValidate()
    {
        if(intent.extras!!.getString("type","").equals("profile",ignoreCase = true) ||intent.extras!!.getString("type","").equals("message",ignoreCase = true))
        {
            mPresenter.reportUser(intent.extras!!.getString("profile_id", "")
                    , bindingObj.contactName.text.toString().trim()
                    , bindingObj.contactEmail.text.toString().trim()
                    , bindingObj.contactPhone.text.toString().trim()
                    , subject
                    , reason)
        }

        else
        {
            mPresenter.reportPortfolio(intent.extras!!.getString("profile_id", "")
                    , bindingObj.contactName.text.toString().trim()
                    , bindingObj.contactEmail.text.toString().trim()
                    , bindingObj.contactPhone.text.toString().trim()
                    , subject
                    , reason
                    ,intent.extras!!.getString("portfolio_url", ""))
        }

    }

    override fun onSuccess()
    {
        if(intent.extras!!.getString("type","").equals("profile",ignoreCase = true) ||intent.extras!!.getString("type","").equals("message",ignoreCase = true))
        Toast.makeText(this , "User has been reported successfully" , Toast.LENGTH_LONG).show()
        else
            Toast.makeText(this , "Portfolio has been reported successfully" , Toast.LENGTH_LONG).show()

        finish()
    }

    override fun showProgress(visiblity: Boolean)
    {

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
                if(isOtherReasonVisible)
                    reason = bindingObj.contactMessage.text.toString().trim()

                if(!reason.equals(""))
                {
                    apiCalling(bindingObj.contactName.text.toString().trim()
                            , bindingObj.contactEmail.text.toString().trim()
                            , bindingObj.contactPhone.text.toString().trim()
                            , subject
                            , reason)
                }
                else
                {
                    showSnackbar("Please select a problem to report")
                }
            }

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter.cancelRetrofitRequest()
    }

}
