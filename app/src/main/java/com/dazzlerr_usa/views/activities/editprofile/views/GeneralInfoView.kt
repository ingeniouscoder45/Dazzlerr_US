package com.dazzlerr_usa.views.activities.editprofile.views

import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo

interface GeneralInfoView
{
    fun onSuccess()
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isValidate()
    fun onGetCountries(dataModel:CountryPojo)
    fun onGetStates(dataModel: StatesPojo, type:Int)//1 for state and 2 for current state
    fun onGetCities(dataModel: CitiesPojo, type:Int)//1 for city and 2 for current city
}
