package com.dazzlerr_usa.views.activities.addjob

import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo

interface AddOrEditJobView
{
    fun onSuccess()
    fun onValidate()
    fun onFailed(message: String)
    fun showProgress(visiblity:Boolean)

    fun onGetCountries(dataModel: CountryPojo)
    fun onGetStates(dataModel: StatesPojo)
    fun onGetCities(dataModel: CitiesPojo)//1 for city and 2 for current city

}