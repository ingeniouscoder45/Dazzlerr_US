package com.dazzlerr_usa.views.fragments.register

interface RegisterPresenter
{

    fun registerModel(user_role_id: String, email:String, password:String, phone:String, name:String ,user_role:String , device_id: String, device_brand: String, device_model: String, device_type: String, operating_system: String)
    fun validateModel(user_role_id: String, email:String, password:String, confirm_password:String, phone:String, fname:String, lname:String )
    fun validateCasting(first_name : String , last_name: String ,email : String , password: String ,confirm_password: String , phone : String , representer:String ,country:String, state: String, city : String, zipcode:String ,whatsppNumber:String)
    fun registerCasting(first_name : String , last_name: String ,email : String, password: String , phone : String , whatsapp : String  , website : String , company : String  , about : String , representer:String ,country:String, state: String, city : String, city_id : String, zipcode:String ,user_role:String )
    fun cancelRetrofitRequest()
    fun getCountries()
    fun getStates(country_id : String)
    fun getCities(state_id :String)
}