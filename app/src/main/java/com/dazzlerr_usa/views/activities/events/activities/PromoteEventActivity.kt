package com.dazzlerr_usa.views.activities.events.activities

import android.app.DatePickerDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityPromoteEventBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener.onItemSelectedListener
import com.dazzlerr_usa.views.activities.events.models.PromoteEventModel
import com.dazzlerr_usa.views.activities.events.pojos.EventsCategoryPojo
import com.dazzlerr_usa.views.activities.events.presenters.PromoteEventPresenter
import com.dazzlerr_usa.views.activities.events.views.PromoteEventView
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class PromoteEventActivity : AppCompatActivity() ,View.OnClickListener , PromoteEventView
{

    lateinit var bindingObj:ActivityPromoteEventBinding
    lateinit var mPresenter: PromoteEventPresenter

    var mEventsCategoryList:ArrayList<String> = ArrayList()
    var mEventsCategoryIdList:ArrayList<String> = ArrayList()
    var mEventCategoryId = ""
    var mEventDate = ""

    lateinit var mCategoryAdapter :ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_promote_event)
        initializations()
        clickListeners()
        apiCalling(1)//getting event categories
    }

    fun initializations() {
        bindingObj.titleLayout.titletxt.text = "Promote Event"
        mPresenter = PromoteEventModel(this, this)


    }

    private fun apiCalling(type:Int)
    {

        if(isNetworkActive()!!)
        {
            if(type==1)
            mPresenter?.getEventsCategory()
            else {
                mPresenter?.validateEvent(mEventCategoryId, bindingObj.promoteEventNameEdittext.text.toString().trim(), mEventDate
                        , bindingObj.promoteEventVenueEdittext.text.toString().trim()
                        , bindingObj.organizerNameEdittext.text.toString().trim()
                        , bindingObj.promoteEventMobileEdittext.text.toString().trim()
                        , bindingObj.promoteEventEmailEdittext.text.toString().trim())
            }

        }

        else
        {

            AlertDialog.Builder(this@PromoteEventActivity)
                    .setTitle("No internet Connection!")
                    .setMessage("Please connect to Mobile network or WiFi.")
                    .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, which ->
                        apiCalling(type)
                    })

                    .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                        dialog.dismiss()
                    })
                    .show()

        }
    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.promoteEventDateText.setOnClickListener(this)
        bindingObj.promoteEventSubmitBtn.setOnClickListener(this)
        bindingObj.eventTypeBtn.setOnClickListener(this)
    }

    override fun onPromoteEventSuccess()
    {

        val mDialog  = CustomDialog(this@PromoteEventActivity)
        mDialog.setTitle("Thanks!")
        mDialog.setMessage("Your event has been successfully submitted to us. We'll contact you within next 24 to 48 hours.")
        mDialog.setPositiveButton("Ok", DialogListenerInterface.onPositiveClickListener {

            mDialog.dismiss()
            finish()
        })
        mDialog.show()
    }

    override fun onEventValidate()
    {
        mPresenter?.promoteEvent(mEventCategoryId
                ,bindingObj.promoteEventNameEdittext.text.toString().trim()
                ,mEventDate ,bindingObj.promoteEventVenueEdittext.text.toString().trim()
                ,bindingObj.organizerNameEdittext.text.toString().trim()
                ,bindingObj.promoteEventMobileEdittext.text.toString().trim()
                ,bindingObj.promoteEventEmailEdittext.text.toString().trim())

    }

    override fun onGetEventCategory(model: EventsCategoryPojo)
    {
        if(model?.result?.size!=0)
        {
            for (i:Int in model?.result!!.indices)
            {
                mEventsCategoryList.add(model?.result?.get(i)?.name.toString())
                mEventsCategoryIdList.add(model?.result?.get(i)?.cat_id.toString())
            }
        }
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    override fun showProgress(visibility: Boolean)
    {
        if (visibility)
            startProgressBarAnim()
        else
            stopProgressBarAnim()
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

    override fun onClick(v: View?)
    {

        when(v?.id)
        {
            R.id.leftbtn->
            {
                finish()
            }
            R.id.promoteEventDateText->
            {
                datePicker(bindingObj.promoteEventDateText)
            }

            R.id.promoteEventSubmitBtn->
            {
                apiCalling(2)// Promote event api
            }

            R.id.eventTypeBtn->
            {
                eventTypePicker()
            }
        }
    }

    fun eventTypePicker()
    {
        val mPicker = ItemPickerDialog(this@PromoteEventActivity , mEventsCategoryList, 0)
        mPicker.onItemSelectedListener(object : onItemSelectedListener {
            override fun onItemSelected(position: Int, selectedValue: String) {

                    mEventCategoryId = mEventsCategoryIdList.get(position)
                    bindingObj.eventTypeSpinnerTxt.text = selectedValue
                    Timber.e("Selected Event id " + mEventCategoryId)

                mPicker.dismiss()
            }
        })

        mPicker.show()
    }
    fun datePicker(textView : TextView)
    {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->

            var month = monthOfYear+1

            mEventDate  = "$dayOfMonth/$month/$year"
            // Display Selected date in textbox
            textView.text = "$dayOfMonth/$month/$year"

        }, year, month, day)

        dpd.getDatePicker().minDate = System.currentTimeMillis();
        dpd.show()
    }

}
