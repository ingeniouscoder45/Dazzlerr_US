package com.dazzlerr_usa.views.activities.featuredccavenue

import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo
import com.dazzlerr_usa.views.activities.membership.GetTokenPojo

interface CCAvenueView
{

    fun onSuccess()
    fun onFailed(message: String)
    fun onGetToken(model : GetTokenPojo)
    fun showProgress(showProgress: Boolean)
    fun isValidate()
    fun onGetStates(dataModel: StatesPojo)
    fun onGetCountries(dataModel: CountryPojo)
    fun onGetCities(dataModel: CitiesPojo)//1 for city and 2 for current city
}
