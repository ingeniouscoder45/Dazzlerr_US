package com.dazzlerr_usa.views.activities.events.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityEventsFilterBinding
import com.dazzlerr_usa.extensions.isNetworkActive
import com.dazzlerr_usa.utilities.customdialogs.CustomDialog
import com.dazzlerr_usa.utilities.customdialogs.DialogListenerInterface
import com.dazzlerr_usa.utilities.itempicker.ItemPickerDialog
import com.dazzlerr_usa.utilities.itempicker.ItemPickerListener
import com.dazzlerr_usa.views.activities.events.models.EventsFilterModel
import com.dazzlerr_usa.views.activities.events.pojos.EventFilterPojo
import com.dazzlerr_usa.views.activities.events.presenters.EventsFilterPresenter
import com.dazzlerr_usa.views.activities.events.views.EventFilterView
import com.google.android.material.snackbar.Snackbar

class EventsFilterActivity : AppCompatActivity() , EventFilterView , View.OnClickListener
{

    lateinit var mPresenter:EventsFilterPresenter
    lateinit var bindingObj: ActivityEventsFilterBinding
    var mCategoryList: ArrayList<String> = ArrayList()
    var mCategoryIdList: ArrayList<Int> = ArrayList()
    var mCategoryId =""
    var mCategoryposition = 0

    var FilterREQUESTCODE = 1001
    var mCityList: ArrayList<String> = ArrayList()
    var mCityPosition =0
    var mCity = ""
    var mOrganizersList: ArrayList<String> = ArrayList()
    var mOrganizerPosition =0
    var mOrganizer = ""
    var mVenueList: ArrayList<String> = ArrayList()
    var mVenuePosition =0
    var mVenue = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_events_filter)
        initializations()
        clickListeners()
        apiCalling()
    }

    fun initializations()
    {
        bindingObj.titleLayout.titletxt.text ="Filters"
        bindingObj.titleLayout.righttxt.text = "Reset Filters"
        bindingObj.titleLayout.righttxt.visibility= View.VISIBLE

        mPresenter = EventsFilterModel(this ,this)

        mCategoryId = intent.extras!!.getString("mCategoryId")!!
        mCity = intent.extras!!.getString("mCity")!!
        mVenue = intent.extras!!.getString("mVenue")!!
        mOrganizer = intent.extras!!.getString("mOrganizer")!!

    }

    fun clickListeners()
    {
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.titleLayout.righttxt.setOnClickListener(this)
        bindingObj.eventCategoryBtn.setOnClickListener(this)
        bindingObj.eventCityBtn.setOnClickListener(this)
        bindingObj.eventVenueBtn.setOnClickListener(this)
        bindingObj.eventOrganizerBtn.setOnClickListener(this)
        bindingObj.applyFilterBtn.setOnClickListener(this)
    }

    fun categorySpinnerSettings()
    {

        val representerPicker = ItemPickerDialog(this@EventsFilterActivity, mCategoryList, mCategoryposition)
        representerPicker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

                mCategoryposition = position
                mCategoryId = mCategoryIdList[position].toString()
                bindingObj.eventCategoryTxt.text = mCategoryList[position]
                representerPicker.dismiss()
            }
        })
        representerPicker.show()
    }


    fun citySpinnerSettings()
    {

        val Picker = ItemPickerDialog(this@EventsFilterActivity, mCityList, mCityPosition)
        Picker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {

                mCityPosition = position
                mCity = mCityList[position]
                bindingObj.eventCityTxt.text = mCityList[position]
                Picker.dismiss()
            }
        })
        Picker.show()
    }

    fun venueSpinnerSettings()
    {

        val Picker = ItemPickerDialog(this@EventsFilterActivity, mVenueList, mVenuePosition)
        Picker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                mVenuePosition = position
                mVenue = mVenueList[position]
                bindingObj.eventVenueTxt.text = mVenueList[position]
                Picker.dismiss()
            }
        })
        Picker.show()

    }
    fun organizerSpinnerSettings()
    {

        val Picker = ItemPickerDialog(this@EventsFilterActivity, mOrganizersList, mOrganizerPosition)
        Picker.onItemSelectedListener(object : ItemPickerListener.onItemSelectedListener
        {
            override fun onItemSelected(position: Int, selectedValue: String)
            {
                mOrganizerPosition = position
                mOrganizer = mOrganizersList[position]
                bindingObj.eventOrganizerTxt.text = mOrganizersList[position]
                Picker.dismiss()
            }
        })
        Picker.show()

    }


    private fun apiCalling()
    {

        if(this@EventsFilterActivity.isNetworkActive())
        {
            mPresenter.getEventsFilters()
        }

        else
        {
            val dialog  = CustomDialog(this@EventsFilterActivity)
            dialog.setTitle(resources.getString(R.string.no_internet_txt))
            dialog.setMessage(resources.getString(R.string.connection_txt))
            dialog.setPositiveButton("Retry", DialogListenerInterface.onPositiveClickListener {
                apiCalling()
            })

            dialog.setNegativeButton("Cancel", DialogListenerInterface.onNegetiveClickListener {
                dialog.dismiss()
            })
            dialog.show()

        }
    }


    override fun onSuccess(model: EventFilterPojo)
    {
        //----------Category lists initializations start
        mCategoryList.clear()
        mCategoryIdList.clear()

        for(i in model?.cat.indices)
        {
            mCategoryList.add(model.cat.get(i).name!!)
            mCategoryIdList.add(model.cat.get(i).cat_id!!)

            if(mCategoryId.equals(model.cat.get(i).cat_id.toString()))
            {
                mCategoryposition = i
                bindingObj.eventCategoryTxt.text = mCategoryList[i]
            }

        }

        //----------Category lists initializations end


        mOrganizersList.clear()
        mOrganizersList.addAll(model.organizer)

        mCityList.clear()
        mCityList.addAll(model.city)

        mVenueList.clear()
        mVenueList.addAll(model.venue)

        for(i in mOrganizersList.indices)
        {
            if(mOrganizer.equals(mOrganizersList[i],ignoreCase = true))
            {
                mOrganizerPosition = i
                bindingObj.eventOrganizerTxt.text = mOrganizersList.get(i)
            }
        }

        for(i in mVenueList.indices)
        {
            if(mVenue.equals(mVenueList[i],ignoreCase = true))
            {
                mVenuePosition = i
                bindingObj.eventVenueTxt.text = mVenueList.get(i)
            }
        }

        for(i in mCityList.indices)
        {
            if(mCity.equals(mCityList[i],ignoreCase = true))
            {
                mCityPosition = i
                bindingObj.eventCityTxt.text = mCityList.get(i)
            }
        }

    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }


    override fun showProgress(visiblity: Boolean)
    {
        if (visiblity)
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
            R.id.leftbtn-> finish()
            R.id.righttxt-> resetFilter()

            R.id.eventCategoryBtn->
            {
                categorySpinnerSettings()
            }

            R.id.eventCityBtn->
            {
                citySpinnerSettings()
            }

            R.id.eventVenueBtn->
            {
                venueSpinnerSettings()
            }

            R.id.eventOrganizerBtn->
            {
                organizerSpinnerSettings()
            }

            R.id.applyFilterBtn->
            {

                val bundle = Bundle()
                bundle.putString("mCategoryId",mCategoryId)
                bundle.putString("mVenue",mVenue)
                bundle.putString("mCity",mCity)
                bundle.putString("mOrganizer",mOrganizer)

                val intent = Intent()
                intent.putExtras(bundle)
                setResult(FilterREQUESTCODE, intent)
                finish()

            }
        }
    }

    fun resetFilter()
    {
        mCity =""
        mVenue =""
        mOrganizer = ""
        mCategoryId= ""

        mCityPosition =0
        mVenuePosition = 0
        mOrganizerPosition = 0
        mCategoryposition = 0

        bindingObj.eventCityTxt.text = ""
        bindingObj.eventCategoryTxt.text = ""
        bindingObj.eventVenueTxt.text = ""
        bindingObj.eventOrganizerTxt.text = ""

    }

}
