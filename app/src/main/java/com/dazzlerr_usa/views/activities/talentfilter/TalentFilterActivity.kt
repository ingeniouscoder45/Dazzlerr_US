package com.dazzlerr_usa.views.activities.talentfilter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityTalentFilterBinding
import io.apptik.widget.MultiSlider
import kotlinx.android.synthetic.main.activity_talent_filter.*
import org.json.JSONException
import org.json.JSONObject
import java.lang.StringBuilder
import java.util.ArrayList
import timber.log.Timber
import java.io.InputStream
import java.lang.Exception

class TalentFilterActivity : AppCompatActivity() , View.OnClickListener
{

    var rangeMinimumAgeVal = ""
    var rangeMinimumHeightVal = ""
    var rangeMaximumAgeVal = ""
    var rangeMaximumHeightVal = ""

    lateinit var categoryAdapter: FilterCategoryAdapter
    lateinit var bindingObj: ActivityTalentFilterBinding
    var exp_type : StringBuilder = StringBuilder()
    var experience_check_list  : ArrayList<String> = ArrayList()
    var gender_check_list  : ArrayList<String> = ArrayList()
    var filter_category_list  : ArrayList<Int> = ArrayList()
    private var citiesList: MutableList<String>? = null
    var city= ""
    var selectedCityPosition=0
    var isCityFound = false


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj  = DataBindingUtil.setContentView(this , R.layout.activity_talent_filter)
        initializations()
        citySpinnerSettings()
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
            rangeMinimumAgeVal = b.getString("minAgeRange")!!
            rangeMaximumAgeVal = b.getString("maxAgeRange")!!
            rangeMinimumHeightVal = b.getString("minHeightRange")!!
            rangeMaximumHeightVal = b.getString("maxHeightRange")!!

            if(!b.getString("fromScreen")!!.equals("Talent"))
            {
                bindingObj.experienceLayout.visibility = View.GONE
                bindingObj.genderLayout.visibility = View.GONE
                bindingObj.filterAgeHeightLayout.visibility = View.GONE
            }
        }

        val categorylist = ArrayList<CategoryPojo>()
        val model1 = CategoryPojo()

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

        categoryAdapter = FilterCategoryAdapter(this, categorylist)

        val gManager = GridLayoutManager(this, 2)

        bindingObj.filterCategoryRecycler.setLayoutManager(gManager)
        bindingObj.filterCategoryRecycler.adapter = categoryAdapter


        if(b?.getString("minAgeRange")!!.isNotEmpty())
        {
            rangeMinimumAgeVal = b.getString("minAgeRange")!!
            bindingObj.ageSlider.getThumb(0).value = rangeMinimumAgeVal.toInt()
            bindingObj.filterMinimumAgeTxt.text = "Min. "+rangeMinimumAgeVal+" Yrs"
        }

        if(b?.getString("maxAgeRange")!!.isNotEmpty())
        {
            rangeMaximumAgeVal = b?.getString("maxAgeRange")!!
            bindingObj.ageSlider.getThumb(1).value = rangeMaximumAgeVal.toInt()
            bindingObj.filterMaximumAgeTxt.text = "Max. "+rangeMaximumAgeVal+" Yrs"
        }


        if(b?.getString("minHeightRange")!!.isNotEmpty())
        {
            rangeMinimumAgeVal = b.getString("minAgeRange")!!
            bindingObj.ageSlider.getThumb(0).value = rangeMinimumHeightVal.toInt()
            bindingObj.filterMinimumHeightTxt.text = "Min. "+rangeMinimumHeightVal+" Feet"
        }

        if(b?.getString("maxHeightRange")!!.isNotEmpty())
        {
            rangeMaximumHeightVal = b?.getString("maxHeightRange")!!
            bindingObj.heightSlider.getThumb(1).value = rangeMaximumHeightVal.toInt()
            bindingObj.filterMaximumHeightTxt.text = "Max. "+rangeMaximumHeightVal+" Feet"
        }


        bindingObj.ageSlider.setMin(5)
        bindingObj.ageSlider.setMax(80)

        bindingObj.ageSlider.setOnThumbValueChangeListener(MultiSlider.OnThumbValueChangeListener { multiSlider, thumb, thumbIndex, value ->
            if (thumbIndex == 0) {
                bindingObj.filterMinimumAgeTxt.text = "Min. "+value.toString()+" Yrs"
                rangeMinimumAgeVal = value.toString()

            } else {

                bindingObj.filterMaximumAgeTxt.text = "Max. "+value.toString()+" Yrs"
                rangeMaximumAgeVal = value.toString()
            }
        })

        bindingObj.heightSlider.setMin(4)
        bindingObj.heightSlider.setMax(7)

        bindingObj.heightSlider.setOnThumbValueChangeListener(MultiSlider.OnThumbValueChangeListener { multiSlider, thumb, thumbIndex, value ->
            if (thumbIndex == 0) {
                bindingObj.filterMinimumHeightTxt.text = "Min. "+value.toString()+" Feet"
                rangeMinimumHeightVal = value.toString()

            } else {

                bindingObj.filterMaximumHeightTxt.text = "Max. "+value.toString()+" Feet"
                rangeMaximumHeightVal = value.toString()
            }
        })
    }

    fun citySpinnerSettings()
    {
        bindingObj.citiesSpinner.setItem(getCities("filtercities.json")!!)
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

    fun createPojo(name : String , id : Int) : CategoryPojo
    {
        val model1 = CategoryPojo()
        model1.title = ""
        model1.type = name
        model1.id = id
        return model1
    }

    fun getCities(file:String) : MutableList<String>
    {
        var CitiesList: MutableList<String> = ArrayList()
        // Exceptions are returned by JSONObject when the object cannot be created
        try {
            // Convert the string returned to a JSON object
            val jsonObject = JSONObject(getJson(file))
            // Get Json array
            val array = jsonObject.getJSONArray("array")

            for (i in 0 until array.length())
            {
                // select the particular JSON data
                val `object` = array.getJSONObject(i)

                CitiesList.add( `object`.getString("city_name"))

                if(city.equals( `object`.getString("city_name"))) {
                    selectedCityPosition = i
                    isCityFound = true
                }


            }

            this.citiesList = CitiesList

        } catch (e: JSONException) {
            e.printStackTrace()
        }


        return CitiesList
    }

    // Get the content of cities.json from assets directory and store it as string
    fun getJson(file:String): String
    {
        var json: String? = null

        try
        {
            val inputStream: InputStream = assets.open(file)
            json = inputStream.bufferedReader().use{it.readText()}
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        return json!!
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

                
                if(bindingObj.ageSlider.getThumb(0).value.toString().equals("5")  && bindingObj.ageSlider.getThumb(1).value.toString().equals("80"))
                {
                    rangeMinimumAgeVal   = ""
                    rangeMaximumAgeVal = ""
                }

                else
                {
                    rangeMinimumAgeVal = bindingObj.ageSlider.getThumb(0).value.toString()
                    rangeMaximumAgeVal = bindingObj.ageSlider.getThumb(1).value.toString()
                }


                if(bindingObj.heightSlider.getThumb(0).value.toString().equals("4")  && bindingObj.heightSlider.getThumb(1).value.toString().equals("7"))
                {
                    rangeMinimumHeightVal   = ""
                    rangeMaximumHeightVal = ""
                }

                else
                {
                    rangeMinimumHeightVal = bindingObj.heightSlider.getThumb(0).value.toString()
                    rangeMaximumHeightVal = bindingObj.heightSlider.getThumb(1).value.toString()
                }

                val bundle = Bundle()
                bundle.putSerializable("checklist", experience_check_list)
                bundle.putSerializable("categoryidsList", filter_category_list)
                bundle.putSerializable("gender_check_list", gender_check_list)
                bundle.putString("city", city)
                bundle.putString("minAgeRange", rangeMinimumAgeVal)
                bundle.putString("maxAgeRange", rangeMaximumAgeVal)
                bundle.putString("minHeightRange", rangeMinimumHeightVal)
                bundle.putString("maxHeightRange", rangeMaximumHeightVal)
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

        rangeMinimumAgeVal = ""
        rangeMaximumAgeVal = ""

        rangeMinimumHeightVal= ""
        rangeMaximumHeightVal= ""

        age_slider.getThumb(0).value = 5
        age_slider.getThumb(1).value = 80

        height_slider.getThumb(0).value = 4
        height_slider.getThumb(1).value = 7
    }
}
