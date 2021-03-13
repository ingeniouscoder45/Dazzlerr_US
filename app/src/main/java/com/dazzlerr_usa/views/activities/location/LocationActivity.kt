package com.dazzlerr_usa.views.activities.location

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.dazzlerr_usa.R
import com.dazzlerr_usa.databinding.ActivityLocationBinding
import com.dazzlerr_usa.utilities.Constants
import com.dazzlerr_usa.utilities.HelperSharedPreferences
import com.dazzlerr_usa.utilities.MyApplication
import com.dazzlerr_usa.views.activities.ContainerActivity
import org.json.JSONObject
import javax.inject.Inject
import java.io.InputStream
import java.lang.Exception
import java.util.ArrayList
import org.json.JSONException
import android.content.Intent




class LocationActivity : ContainerActivity()  , View.OnClickListener
{
    lateinit var bindingObj : ActivityLocationBinding
    @Inject
    lateinit var sharedPreferences : HelperSharedPreferences

    lateinit var othercitiesAdapter : OtherCitiesAdapter

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        bindingObj = DataBindingUtil.setContentView(this , R.layout.activity_location)
        initializations()
        searchView()
    }

    internal fun initializations()
    {
        (application as MyApplication).myComponent.inject(this)

        bindingObj.titleLayout.rightbtn.visibility = View.GONE
        bindingObj.titleLayout.leftbtn.setBackgroundResource(R.drawable.ic_back_white_32dp)
        bindingObj.titleLayout.titletxt.text = sharedPreferences.getString(Constants.CurrentAddress)
        bindingObj.titleLayout.leftbtn.setOnClickListener(this)
        bindingObj.detectLocationBtn.setOnClickListener(this)


        //-----Populating the data on Other Cities list
        othercitiesAdapter = OtherCitiesAdapter(this , getCities("cities.json"))
        bindingObj.otherCitiesRecycler.layoutManager = LinearLayoutManager(this)
        bindingObj.otherCitiesRecycler.adapter = othercitiesAdapter
    }

    fun searchView()
    {
        // listening to search query text change
        bindingObj.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                othercitiesAdapter.getFilter().filter(query)


                return false
            }

            override fun onQueryTextChange(query: String): Boolean {

                if(query.length==0)
                {
                    othercitiesAdapter.getFilter().filter(query)
                }
                else {

                    // filter recycler view when text is changed
                    othercitiesAdapter.getFilter().filter(query)
                }

                return false
            }
        })

    }

    fun getCities(file:String) : ArrayList<LocationPojo>
    {
         var CitiesList: ArrayList<LocationPojo> = ArrayList()
        // Exceptions are returned by JSONObject when the object cannot be created
        try {
            // Convert the string returned to a JSON object
            val jsonObject = JSONObject(getJson(file))
            // Get Json array
            val array = jsonObject.getJSONArray("array")
            var locationPojo :LocationPojo
            // Navigate through an array item one by one
            for (i in 0 until array.length()) {
                // select the particular JSON data
                val `object` = array.getJSONObject(i)
                locationPojo = LocationPojo()
                locationPojo.cityName = `object`.getString("city_name")
                CitiesList.add(locationPojo)

            }
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
            val inputStream:InputStream = assets.open(file)
            json = inputStream.bufferedReader().use{it.readText()}
        }
        catch (e:Exception)
        {
            e.printStackTrace()
        }

        return json!!
    }


    override fun onLocationfetched(latitude: Double, longitude: Double, address: String?)
    {
        sharedPreferences.saveString(Constants.Latitude, latitude.toString())
        sharedPreferences.saveString(Constants.Longitude, longitude.toString())
        sharedPreferences.saveString(Constants.CurrentAddress, address!!)

        bindingObj.titleLayout.titletxt.text = address

        Toast.makeText(this , "Location fetched successfully" , Toast.LENGTH_SHORT).show()

        val intent = Intent()
        intent.putExtra("current_address", address)
        setResult(100, intent)
        finish()

    }

    override fun onClick(v: View?)
    {
        when(v?.id)
        {
            R.id.leftbtn ->
            {
                finish()
            }

            R.id.detectLocationBtn->
            {
               // Snackbar.make(findViewById(android.R.id.content) , "Please wait! while we are detecting your current location" , Snackbar.LENGTH_SHORT).show()
                super.getLocation()
            }
        }
    }

}
