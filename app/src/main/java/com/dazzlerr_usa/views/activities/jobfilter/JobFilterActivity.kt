package com.dazzlerr_usa.views.activities.jobfilter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityTalentFilterBinding
import com.dazzlerr_usa.extensions.isNetworkActiveWithMessage
import com.google.android.material.snackbar.Snackbar
import com.onlinepoundstore.fragments.login.JobFilterView
import java.lang.StringBuilder
import java.util.ArrayList
import timber.log.Timber

class JobFilterActivity : AppCompatActivity() , View.OnClickListener , JobFilterView
{

    lateinit var categoryAdapter: JobFilterCategoryAdapter
    lateinit var bindingObj: ActivityTalentFilterBinding
    var exp_type : StringBuilder = StringBuilder()
    var experience_check_list  : ArrayList<String> = ArrayList()
    var gender_check_list  : ArrayList<String> = ArrayList()
    var filter_category_list  : ArrayList<Int> = ArrayList()
    private var citiesList: MutableList<String>? = ArrayList()
    var city= ""
    var selectedCityPosition=0
    var isCityFound = false
    var mPresenter:JobFilterPresenter ? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_talent_filter)
        initializations()
        apiCalling()
        //citySpinnerSettings()
        setChecklist()
        clickListeneres()
        expTypeBuilder()
        genderBuilder()
    }

    fun initializations()
    {
        val b = intent.extras
        if (null != b)
        {
            experience_check_list = b.getStringArrayList("checklist")!!
            gender_check_list = b.getStringArrayList("gender_check_list")!!
            filter_category_list = b.getIntegerArrayList("categoryidsList")!!
            city = b.getString("city")!!

            if(!b.getString("fromScreen")!!.equals("Talent"))
            {
                bindingObj.experienceLayout.visibility = View.GONE
                bindingObj.genderLayout.visibility = View.GONE
                bindingObj.filterAgeHeightLayout.visibility = View.GONE
            }
        }

        mPresenter  = JobFilterModel(this , this)
        val categorylist = ArrayList<JobCategoryPojo>()
        val model1 = JobCategoryPojo()

        model1.title = ""
        model1.type = "Model"
        model1.id = 2

        categorylist.add(createPojo("Model"  , 2))
        categorylist.add(createPojo("Stylist"  , 5))
        categorylist.add(createPojo("Photographer"  , 3))
        categorylist.add(createPojo("Anchor"  , 8))
        categorylist.add(createPojo("Comedian"  , 13))
        categorylist.add(createPojo("Dancer"  , 9))
        categorylist.add(createPojo("Actor"  , 11))
        categorylist.add(createPojo("Singer"  , 14))
        categorylist.add(createPojo("Hair Stylist"  , 7))
        categorylist.add(createPojo("Makeup Artist"  , 4))
        categorylist.add(createPojo("Fashion Designer"  , 10))
        categorylist.add(createPojo("Modelling Choreographer"  , 12))

        categoryAdapter = JobFilterCategoryAdapter(this, categorylist)

        val gManager = GridLayoutManager(this, 2)

        bindingObj.filterCategoryRecycler.setLayoutManager(gManager)
        bindingObj.filterCategoryRecycler.adapter = categoryAdapter


    }

    fun apiCalling()
    {

        if(isNetworkActiveWithMessage())
        {
            mPresenter?.getJobCities()
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

    fun clickListeneres()
    {
        bindingObj.resetFilterBtn.setOnClickListener (this)
        bindingObj.applyFiltersBtn.setOnClickListener (this)
        bindingObj.backBtn.setOnClickListener (this)
    }

    fun createPojo(name : String , id : Int) : JobCategoryPojo
    {
        val model1 = JobCategoryPojo()
        model1.title = ""
        model1.type = name
        model1.id = id
        return model1
    }




    fun expTypeBuilder()
    {
        bindingObj.filterExpLvlNovice.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!experience_check_list.contains("Novice"))
                experience_check_list.add("Novice")
            }
            else
            {
               if(experience_check_list.contains("Novice"))
               {
                   experience_check_list.remove("Novice")
               }
            }

            Timber.e(experience_check_list.toString())
        }

        bindingObj.filterExpLvlExperienced.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!experience_check_list.contains("Experienced"))
                experience_check_list.add("Experienced")
            }
            else
            {
                if(experience_check_list.contains("Experienced"))
                {
                    experience_check_list.remove("Experienced")
                }
            }

            Timber.e(experience_check_list.toString())
        }

        bindingObj.filterExpLvlProfessional.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!experience_check_list.contains("Professional"))
                experience_check_list.add("Professional")
            }
            else
            {
                if(experience_check_list.contains("Professional"))
                {
                    experience_check_list.remove("Professional")
                }
            }

            Timber.e(experience_check_list.toString())
        }

    }

    fun genderBuilder()
    {
        bindingObj.filterGenderMale.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!gender_check_list.contains("Male"))
                    gender_check_list.add("Male")
            }
            else
            {
                if(gender_check_list.contains("Male"))
                {
                    gender_check_list.remove("Male")
                }
            }
            Timber.e(gender_check_list.toString())
        }

        bindingObj.filterGenderFemale.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked)
            {
                if(!gender_check_list.contains("Female"))
                    gender_check_list.add("Female")
            }
            else
            {
                if(gender_check_list.contains("Female"))
                {
                    gender_check_list.remove("Female")
                }
            }
            Timber.e(gender_check_list.toString())
        }

    }


    override fun onGetJobCities(dataModel: JobFilterPojo) {

        citiesList?.clear()
        for(i in dataModel.data?.indices!!)
        {
            citiesList?.add(dataModel?.data?.get(i)?.city!!)

            if(city.equals( dataModel?.data?.get(i)?.city!!)) {
                selectedCityPosition = i
                isCityFound = true
            }
        }

        citySpinnerSettings()
    }

    override fun onFailed(message: String)
    {
        showSnackbar(message)
    }

    fun showSnackbar(message : String)
    {
        Snackbar.make(findViewById(android.R.id.content) , message , Snackbar.LENGTH_SHORT ).show()
    }
    override fun showProgress(showProgress: Boolean)
    {
        if(showProgress)
            startProgressBarAnim()

        else
            stopProgressBarAnim()
    }


    fun startProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.VISIBLE
    }

    fun stopProgressBarAnim() {

        bindingObj.aviProgressBar.visibility = View.GONE
    }


    override fun onClick(v: View?) {

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
                bundle.putSerializable("checklist", experience_check_list)
                bundle.putSerializable("categoryidsList", filter_category_list)
                bundle.putSerializable("gender_check_list", gender_check_list)
                bundle.putString("city", city)
                val intent = Intent()
                intent.putExtras(bundle)
                setResult(101, intent)
                finish()
            }
        }
    }

    fun setChecklist()
    {

        for(i:Int in experience_check_list.indices)
        {
            if(experience_check_list.get(i).equals("Novice"))
                bindingObj.filterExpLvlNovice.isChecked = true

            else if(experience_check_list.get(i).equals("Experienced"))
                bindingObj.filterExpLvlExperienced.isChecked = true

            else if(experience_check_list.get(i).equals("Professional"))
                bindingObj.filterExpLvlProfessional.isChecked = true

        }

        for(i:Int in gender_check_list.indices)
        {
            if(gender_check_list.get(i).equals("Male"))
                bindingObj.filterGenderMale.isChecked = true

            else if(gender_check_list.get(i).equals("Female"))
                bindingObj.filterGenderFemale.isChecked = true

        }

    }

    fun resetFilters()
    {
        bindingObj.filterExpLvlNovice.isChecked = false
        bindingObj.filterExpLvlExperienced.isChecked = false
        bindingObj.filterExpLvlProfessional.isChecked = false
        bindingObj.filterGenderMale.isChecked = false
        bindingObj.filterGenderFemale.isChecked = false
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
