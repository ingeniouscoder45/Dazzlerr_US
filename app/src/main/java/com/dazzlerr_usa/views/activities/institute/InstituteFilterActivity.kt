package com.dazzlerr_usa.views.activities.institute

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityInstituteFilterBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.dazzlerr_usa.views.activities.jobfilter.JobCategoryPojo
import com.google.android.material.snackbar.Snackbar
import java.util.ArrayList

class InstituteFilterActivity : AppCompatActivity() , View.OnClickListener , InstituteView
{

    lateinit var categoryAdapter: InstituteFilterCategoryAdapter
    lateinit var bindingObj:ActivityInstituteFilterBinding
    var filter_category_list  : ArrayList<Int> = ArrayList()
    private var citiesList: MutableList<String>? = ArrayList()
    var city= ""
    var selectedCityPosition=0
    var isCityFound = false
    lateinit var mPresenter: InstitutePresenter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj =DataBindingUtil.setContentView(this , R.layout.activity_institute_filter)
        initializations()
        apiCalling()
        clickListeneres()
    }

    fun initializations()
    {
        val b = intent.extras
        if (null != b)
        {

            filter_category_list = b.getIntegerArrayList("categoryidsList")!!
            city = b.getString("city")!!


        }

        mPresenter  = InstituteModel(this , this)
        val model1 = JobCategoryPojo()

        model1.title = ""
        model1.type = "Model"
        model1.id = 2



        val gManager = GridLayoutManager(this, 2)

        bindingObj.filterCategoryRecycler.setLayoutManager(gManager)

    }

    fun clickListeneres()
    {
        bindingObj.resetFilterBtn.setOnClickListener(this)
        bindingObj.applyFiltersBtn.setOnClickListener(this)
        bindingObj.backBtn.setOnClickListener(this)
    }

    fun apiCalling()
    {

        if(isNetworkActiveWithMessage())
        {
            mPresenter?.getInstitutez("", "" , "0" ,true )
        }
    }


    fun citySpinnerSettings()
    {
        bindingObj.citiesSpinner.setItem(citiesList!!)
        if(isCityFound)
            bindingObj.citiesSpinner.setSelection(selectedCityPosition)

        bindingObj.citiesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, position: Int, id: Long) {
                // Toast.makeText(this@TalentFilterActivity, citiesList!![position], Toast.LENGTH_SHORT).show()
                city = citiesList!![position]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
    }

    override fun onSuccess(model: InstitutePojo)
    {

        citiesList?.clear()
        for(i in model.filter_city?.indices!!)
        {
            citiesList?.add(model?.filter_city?.get(i)?.city!!)

            if(city.equals( model?.filter_city?.get(i)?.city!!)) {
                selectedCityPosition = i
                isCityFound = true
            }
        }

        citySpinnerSettings()




        categoryAdapter = InstituteFilterCategoryAdapter(this, model.filter_cat)
        bindingObj.filterCategoryRecycler.adapter = categoryAdapter
    }



    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }


    override fun showProgress(visiblity: Boolean , isShowProgressbar: Boolean)
    {

        if(isShowProgressbar) {
            if (visiblity)
                startProgressBarAnim()
            else
                stopProgressBarAnim()
        }

    }


    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }


    override fun onClick(v: View?)
    {

        when(v?.id)
        {

            R.id.resetFilterBtn->
            {
                resetFilters()
            }

            R.id.backBtn-> finish()

            R.id.applyFiltersBtn->
            {
                val bundle = Bundle()
                bundle.putSerializable("categoryidsList", filter_category_list)
                bundle.putString("city", city)
                val intent = Intent()
                intent.putExtras(bundle)
                setResult(101, intent)
                finish()
            }
        }
    }


    fun resetFilters()
    {

        filter_category_list.clear()
        bindingObj.filterCategoryRecycler.adapter = categoryAdapter
        city = ""
        isCityFound = false
        selectedCityPosition =0
        citySpinnerSettings()
    }

    override fun onDestroy() {
        super.onDestroy()

        mPresenter?.cancelRetrofitRequest()
    }
}