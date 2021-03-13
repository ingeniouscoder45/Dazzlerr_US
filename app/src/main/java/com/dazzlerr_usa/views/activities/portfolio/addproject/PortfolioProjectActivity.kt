package com.dazzlerr_usa.views.activities.portfolio.addproject

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityPortfolioProjectBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.utilities.Utils
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.google.android.material.snackbar.Snackbar
import java.util.*
import javax.inject.Inject

class PortfolioProjectActivity : AppCompatActivity() , View.OnClickListener , ProjectView
{

    @Inject
    lateinit var sharedPreferences: HelperSharedPreferences

    lateinit var bindingObj : ActivityPortfolioProjectBinding
    lateinit var mPresenter : ProjectPresenter
    var project_id = ""


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this ,R.layout.activity_portfolio_project)
        initializations()
        clickListeners()
    }

    fun initializations()
    {
        //----------------Injecting dagger into shared Preferences
        (application as MyApplication).myComponent.inject(this)

        bindingObj.titleLayout.titletxt.setText("Add Project")
        mPresenter  = ProjectModel(this , this)
        
        if (intent.extras != null && intent.extras!!.containsKey("project_id"))
        {
            bindingObj.titleLayout.titletxt.setText("Edit Project")
            project_id = intent.extras!!.getString("project_id", "")

            bindingObj.ProjectTitleEdittext.setText(intent.extras!!.getString("project_title", ""))
            bindingObj.ProjectStartDateText.setText(intent.extras!!.getString("project_startdate", ""))
            bindingObj.ProjectEndDateText.setText(intent.extras!!.getString("project_enddate", ""))
            bindingObj.ProjectRoleEdittext.setText(intent.extras!!.getString("project_role", ""))
            bindingObj.ProjectDescriptionEdittext.setText(intent.extras!!.getString("project_description", ""))
        }


        bindingObj.ProjectDescriptionEdittext?.addTextChangedListener(object : TextWatcher
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
                    bindingObj.characterLeftTxt?.text = "Characters left: " + (500 - s.length)
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
        bindingObj.projectSubmitBtn.setOnClickListener(this)
        bindingObj.ProjectStartDateText.setOnClickListener(this)
        bindingObj.ProjectEndDateText.setOnClickListener(this)
    }

    fun datePicker(type : Int, textView : TextView)// 1 for start date and 2 for end date
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            var month = monthOfYear+1
            // Display Selected date in textbox
            if(type==1) {
                textView.text = "" + dayOfMonth + "/" + month + "/" + year

                if(bindingObj.ProjectEndDateText.text.trim().isNotEmpty())
                {
                    if(Utils.compareDate("" + dayOfMonth + "/" + month + "/" + year ,bindingObj.ProjectEndDateText.text.toString() ))
                    {
                        bindingObj.ProjectEndDateText.setText("")
                    }

                }
            }

            else if(type ==2)
            {
                var d = "" + dayOfMonth + "/" + month + "/" + year

                if(bindingObj.ProjectStartDateText.text.equals(d))
                {
                    textView.text = "" + dayOfMonth + "/" + month + "/" + year
                }
                else
                {
                    if (Utils.compareDate(bindingObj.ProjectStartDateText.text.toString(), "" + dayOfMonth + "/" + month + "/" + year)) {
                        showSnackbar("Please select a end date of the project after the start date")
                    } else
                        textView.text = "" + dayOfMonth + "/" + month + "/" + year


                }
            }

        }, year, month, day)

        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.show()
    }


    override fun onSuccess() {

        var message  = ""
        if(project_id.length==0)
            message = "Project has been added successfully."
        else
            message = "Project has been edited successfully."


        val dialog  = CustomDialog(this@PortfolioProjectActivity)
                dialog.setTitle("Alert!")
                dialog.setMessage(message)
                dialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

                    sendResult()
                })
                dialog.show()
    }

    override fun onFailed(message: String)
    {

        showSnackbar(message)
    }

    override fun showProgress(showProgress: Boolean) {

        if(showProgress)
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
            Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG).show()
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun sendResult()
    {
        val bundle = Bundle()
        bundle.putString("extras" , "true")
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(100, intent)
        finish()
    }


    override fun isValidate() {

        if(project_id.length==0)
        {
            mPresenter.addProject(sharedPreferences.getString(Constants.User_id).toString()
                    , bindingObj.ProjectTitleEdittext.text.toString().trim()
                    , bindingObj.ProjectRoleEdittext.text.toString().trim()
                    , bindingObj.ProjectStartDateText.text.toString().trim()
                    , bindingObj.ProjectEndDateText.text.toString().trim()
                    , bindingObj.ProjectDescriptionEdittext.text.toString().trim())
        }

        else
        {
            mPresenter.updateProject(sharedPreferences.getString(Constants.User_id).toString()
                    , project_id
                    , bindingObj.ProjectTitleEdittext.text.toString().trim()
                    , bindingObj.ProjectRoleEdittext.text.toString().trim()
                    , bindingObj.ProjectStartDateText.text.toString().trim()
                    , bindingObj.ProjectEndDateText.text.toString().trim()
                    , bindingObj.ProjectDescriptionEdittext.text.toString().trim())
        }
    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.projectSubmitBtn->
            {
                mPresenter.validate(bindingObj.ProjectTitleEdittext.text.toString().trim()
                        , bindingObj.ProjectRoleEdittext.text.toString().trim()
                        , bindingObj.ProjectStartDateText.text.toString().trim()
                        , bindingObj.ProjectEndDateText.text.toString().trim()
                        , bindingObj.ProjectDescriptionEdittext.text.toString().trim())
            }

            R.id.leftbtn-> finish()

            R.id.ProjectStartDateText-> datePicker(1 ,bindingObj.ProjectStartDateText)
            R.id.ProjectEndDateText->
            {
                if(bindingObj.ProjectStartDateText.text.trim().isNotEmpty())
                {
                    datePicker(2 ,bindingObj.ProjectEndDateText)
                }
                else
                {
                    showSnackbar("Please select start date first")
                }
            }
        }
    }

}
