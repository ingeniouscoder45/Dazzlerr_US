package com.dazzlerr_usa.views.fragments.register

import com.dazzlerr_usa.views.activities.editprofile.pojos.CitiesPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.CountryPojo
import com.dazzlerr_usa.views.activities.editprofile.pojos.StatesPojo

interface RegisterView
{
    fun onRegisterSuccess(dataModel: MutableList<RegisterPojo.DataBean>)
    fun onFailed(message: String)
    fun showProgress(showProgress: Boolean)
    fun isRegisterModelValidate(isValid: Boolean)
    fun isRegisterCastingValidate(isValid: Boolean)
    fun onGetCountry(dataModel: CountryPojo)
    fun onGetStates(dataModel: StatesPojo)
    fun onGetCities(dataModel: CitiesPojo)//1 for city and 2 for current city
}
