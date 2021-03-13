package com.dazzlerr_usa.views.activities.editprofile.presenters

interface GeneralInformationPresenter
{
    fun updateGeneralInformation(user_id: String , email:String, user_role_id:String , name:String , phone:String , whats_app:String,country_id : String
                                 , state_id:String, city:String, exp_type:String
                                 , languages:String, dob:String, marital_status:String, agency_signed:String, available_for:String
                                 , gender:String, pref_location:String, about:String, interested_in:String, skills:String, agency_name:String ,is_public:String ,parent_name :String , parent_contact:String , agency_phone:String , agency_email:String , username:String)

    fun validate(phone:String , whats_app:String,country_id : String
                 , state_id:String, city:String, exp_type:String
                 , languages:String, marital_status:String, agency_signed:String, available_for:String
                 , gender:String, pref_location:String, isAgeRestrict:Boolean , parent_name :String , parent_contact:String)

    fun cancelRetrofitRequest()
    fun getCountries()
    fun getStates(country_id :String , type:Int)//1 for state and 2 for current state
    fun getCities(state_id :String , type:Int)//1 for city and 2 for current city
}