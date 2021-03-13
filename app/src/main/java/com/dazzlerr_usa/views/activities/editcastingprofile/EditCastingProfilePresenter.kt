package com.dazzlerr_usa.views.activities.editcastingprofile

interface EditCastingProfilePresenter
{
    fun updateCastingProfile(name : String , company_name: String ,representer : String , about: String ,country_id : String ,state_id: String , zipcode : String , phone:String , whats_app: String, website : String, facebook:String, twitter:String, instagram:String, city:String , city_id:String , user_id:String)
    fun validateCastingProfile(name :String  ,representer : String ,country_id : String ,state_id: String , phone:String, city:String ,zipcode:String ,email : String , whatsapp: String , website :String, facebook : String , twitter: String , instagram :String)
    fun cancelRetrofitRequest()
    fun getCountries()
    fun getStates(country_id : String)
    fun getCities(state_id :String)
}